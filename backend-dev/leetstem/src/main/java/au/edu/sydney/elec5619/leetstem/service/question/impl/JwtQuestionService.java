package au.edu.sydney.elec5619.leetstem.service.question.impl;

import au.edu.sydney.elec5619.leetstem.constant.Difficulty;
import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import au.edu.sydney.elec5619.leetstem.dto.AttemptDetail;
import au.edu.sydney.elec5619.leetstem.dto.PaginatedQuestionMeta;
import au.edu.sydney.elec5619.leetstem.dto.QuestionDetail;
import au.edu.sydney.elec5619.leetstem.dto.QuestionMeta;
import au.edu.sydney.elec5619.leetstem.entity.Attempt;
import au.edu.sydney.elec5619.leetstem.entity.Question;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.*;
import au.edu.sydney.elec5619.leetstem.service.db.QuestionAttemptService;
import au.edu.sydney.elec5619.leetstem.service.db.UserStatsService;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import au.edu.sydney.elec5619.leetstem.service.question.QuestionService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtQuestionService implements QuestionService {
    private final StatefulJwtService simpleStatefulJwtService;
    private final QuestionAttemptService jpaQuestionAttemptService;
    private final UserStatsService jpaUserStatsService;
    public JwtQuestionService(StatefulJwtService simpleStatefulJwtService,
                              QuestionAttemptService jpaQuestionAttemptService,
                              UserStatsService jpaUserStatsService) {
        this.simpleStatefulJwtService = simpleStatefulJwtService;
        this.jpaQuestionAttemptService = jpaQuestionAttemptService;
        this.jpaUserStatsService = jpaUserStatsService;
    }
    @Override
    public PaginatedQuestionMeta getPaginatedQuestionMetaFilteredBy(Subject subject, Topic topic, Difficulty difficulty,
                                                                    String token, Boolean passed, Integer pageSize,
                                                                    Integer pageNo) throws ApiException {
        Integer userId = null;
        if (token != null) {
            try {
                userId = Integer.parseInt(simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(token,
                        JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS));
            } catch (UnsupportedJwtException |
                     MalformedJwtException |
                     ExpiredJwtException |
                     IllegalArgumentException |
                     SignatureException e) {
                throw new BadTokenException();
            }
        }

        pageNo = pageNo == null ? 0 : pageNo;
        pageSize = pageSize == null ? 10 : pageSize;

        if (pageNo < 0 || pageSize < 0) {
            throw new BadPaginationParametersException();
        }

        Page<Question> questions = jpaQuestionAttemptService
                .getPaginatedFilteredQuestions(pageSize, pageNo, subject, topic, difficulty, userId, passed);

        List<QuestionMeta> questionMetas = new ArrayList<>();
        for (Question question : questions) {
            boolean questionPassed = false;
            if (null != userId) {
                questionPassed = jpaQuestionAttemptService.hasCorrectAttemptsOfQuestion(userId, question.getId());
            }
            questionMetas.add(new QuestionMeta(
                    question.getId().toString(),
                    question.getTitle(),
                    question.getType(),
                    question.getDifficulty(),
                    question.getTopic().toString(),
                    questionPassed
            ));
        }

        return new PaginatedQuestionMeta(
                questionMetas,
                pageSize,
                pageNo,
                questions.isLast()
        );
    }

    @Override
    public void checkQuestionAttempt(Integer userId, String questionId) throws ApiException {
        // parameter check
        if (questionId == null || questionId.isEmpty()) {
            throw new BadQuestionIdException();
        }

        // check attempt
        int questionIdInt;
        try {
            questionIdInt = Integer.parseInt(questionId);
        } catch (NumberFormatException e) {
            throw new BadQuestionIdException();
        }

        boolean hasAttempts = jpaQuestionAttemptService.hasAttemptsOfQuestion(userId, questionIdInt);
        if (!hasAttempts) {
            throw new NoQuestionAttemptException(); // no attempt
        }
    }

    @Override
    public QuestionDetail getQuestionById(int questionId, String accessToken) throws ApiException{
        Question question = jpaQuestionAttemptService.getQuestionById(questionId);
        if (null == question) {
            throw new BadQuestionIdException();
        }
        boolean isQuestionAttempted = false;
        if (null != accessToken) {
            try {
                String userId = simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(
                        accessToken,
                        JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS);
                isQuestionAttempted = jpaQuestionAttemptService.hasAttemptsOfQuestion(
                        Integer.parseInt(userId),
                        question.getId());
            } catch (UnsupportedJwtException |
                     MalformedJwtException |
                     ExpiredJwtException |
                     IllegalArgumentException |
                     SignatureException ignored) {
                // Exception ignored because this feature doesn't require any privilege.
            }
        }
        return new QuestionDetail(
                question.getId().toString(),
                isQuestionAttempted,
                question.getDifficulty(),
                question.getTitle(),
                question.getType(),
                question.getBody()
        );
    }

    @Override
    public boolean attempt(int questionId, String attemptBody, String accessToken) throws ApiException {
        Question question = jpaQuestionAttemptService.getQuestionById(questionId);
        if (null == question) {
            throw new BadQuestionIdException();
        }

        if (null == attemptBody) {
            throw new NullAttemptBodyException();
        }

        Integer userId = null;
        if (null != accessToken) {
            try {
                userId = Integer.parseInt(
                        simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(
                                accessToken,
                                JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS)
                );
            } catch (UnsupportedJwtException |
                     MalformedJwtException |
                     ExpiredJwtException |
                     IllegalArgumentException |
                     SignatureException ignored) {
                // Exception ignored because this feature doesn't require any privilege.
            }
        }
        // Compare answers
        boolean isAttemptCorrect = question.getAnswer().equals(attemptBody);
        // Record only when logged in
        if (null != userId) {
            boolean isFirstAttempt = !jpaQuestionAttemptService.hasAttemptsOfQuestion(userId, questionId);
            if (isFirstAttempt) {
                // Increment attempted questions of the subject
                jpaUserStatsService.incrementSubjectAttempt(userId, question.getSubject());
                jpaUserStatsService.incrementTotalAttempt(userId);
            }
            if (isAttemptCorrect) {
                // Only increment total correct if no correct attempts were made before
                if (!jpaQuestionAttemptService.hasCorrectAttemptsOfQuestion(userId, questionId)) {
                    jpaUserStatsService.incrementSubjectTotalCorrect(userId, question.getSubject());
                    jpaUserStatsService.incrementTotalCorrect(userId);
                }
            }
            if (isFirstAttempt && isAttemptCorrect) {
                // Increment first correct of the subject
                jpaUserStatsService.incrementSubjectFirstCorrect(userId, question.getSubject());
            }

            // Save attempt
            jpaQuestionAttemptService.insertNewAttempt(
                    userId,
                    questionId,
                    attemptBody,
                    isAttemptCorrect);

            // earn badge
            if (isAttemptCorrect) {
                jpaUserStatsService.earnBadge(userId, question.getSubject());
            }
        }

        // Record stats
        return isAttemptCorrect;
    }

    @Override
    public AttemptDetail getLatestAttemptByQuestionId(String token, int questionId) throws ApiException {
        int userId;
        try {
            userId = Integer.parseInt(
                    simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(
                            token,
                            JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS)
            );
        } catch (UnsupportedJwtException |
                 MalformedJwtException |
                 ExpiredJwtException |
                 IllegalArgumentException |
                 SignatureException e) {
            throw new UnauthenticatedUserException();
        }
        if (jpaQuestionAttemptService.getQuestionById(questionId) == null) {
            throw new BadQuestionIdException();
        }
        Attempt attempt = jpaQuestionAttemptService.getLatestAttemptByQuestionId(userId, questionId);
        if (null == attempt) {
            throw new NoQuestionAttemptException();
        }
        return new AttemptDetail(
                attempt.getAnswer(),
                attempt.isCorrect()
        );
    }
}
