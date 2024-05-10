package au.edu.sydney.elec5619.leetstem.service.db.impl;

import au.edu.sydney.elec5619.leetstem.constant.Difficulty;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import au.edu.sydney.elec5619.leetstem.entity.Attempt;
import au.edu.sydney.elec5619.leetstem.entity.Question;
import au.edu.sydney.elec5619.leetstem.repository.AttemptRepository;
import au.edu.sydney.elec5619.leetstem.repository.QuestionRepository;
import au.edu.sydney.elec5619.leetstem.service.db.QuestionAttemptService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JpaQuestionAttemptService implements QuestionAttemptService {
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    public JpaQuestionAttemptService(QuestionRepository questionRepository,
                                     AttemptRepository attemptRepository) {
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
    }

    @Override
    public Page<Question> getPaginatedFilteredQuestions(int pageSize, int pageNo, Subject subject, Topic topic,
                                                        Difficulty difficulty, Integer userId, Boolean passed) {
        return questionRepository.findByNullableFilters(
                subject.getId(),
                topic == null ? null : topic.getId(),
                difficulty == null ? null : difficulty.getId(),
                passed, userId, PageRequest.of(pageNo, pageSize));
    }

    @Override
    public boolean hasAttemptsOfQuestion(Integer userId, int questionId) {
        return !attemptRepository.findByUserIdAndQuestionId(userId, questionId).isEmpty();
    }

    @Override
    public Question getQuestionById(int questionId) {
        return questionRepository.findById(questionId);
    }

    @Override
    public void insertNewAttempt(int userId, int questionId, String attemptBody, boolean isCorrect) {
        Attempt attempt = new Attempt();
        attempt.setQuestionId(questionId);
        attempt.setAnswer(attemptBody);
        attempt.setUserId(userId);
        attempt.setCorrect(isCorrect);
        attemptRepository.save(attempt);
    }

    @Override
    public boolean hasCorrectAttemptsOfQuestion(int userId, int questionId) {
        return !attemptRepository.findByUserIdAndQuestionIdAndIsCorrect(userId, questionId, true).isEmpty();
    }

    @Override
    public Attempt getLatestAttemptByQuestionId(int userId, int questionId) {
        return attemptRepository.findFirstByUserIdAndQuestionIdOrderByCreatedAtDesc(userId, questionId);
    }
}
