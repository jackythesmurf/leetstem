package au.edu.sydney.elec5619.leetstem.service.question.impl;

import au.edu.sydney.elec5619.leetstem.constant.*;
import au.edu.sydney.elec5619.leetstem.dto.AttemptDetail;
import au.edu.sydney.elec5619.leetstem.dto.PaginatedQuestionMeta;
import au.edu.sydney.elec5619.leetstem.dto.QuestionDetail;
import au.edu.sydney.elec5619.leetstem.dto.QuestionMeta;
import au.edu.sydney.elec5619.leetstem.entity.Attempt;
import au.edu.sydney.elec5619.leetstem.entity.Question;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadQuestionIdException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadTokenException;
import au.edu.sydney.elec5619.leetstem.exception.impl.NoQuestionAttemptException;
import au.edu.sydney.elec5619.leetstem.exception.impl.UnauthenticatedUserException;
import au.edu.sydney.elec5619.leetstem.service.db.QuestionAttemptService;
import au.edu.sydney.elec5619.leetstem.service.db.UserStatsService;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JwtQuestionServiceTest {
    private StatefulJwtService mockedStatefulJwtService;
    private QuestionAttemptService mockedQuestionAttemptService;
    private UserStatsService mockedUserStatsService;
    private JwtQuestionService jwtQuestionService;
    private Random random;
    @BeforeEach
    void setup() {
        mockedStatefulJwtService = Mockito.mock(StatefulJwtService.class);
        mockedQuestionAttemptService = Mockito.mock(QuestionAttemptService.class);
        mockedUserStatsService = Mockito.mock(UserStatsService.class);
        jwtQuestionService = new JwtQuestionService(
                mockedStatefulJwtService,
                mockedQuestionAttemptService,
                mockedUserStatsService);
        random = new Random(5619);
    }

    @Nested
    class GetPaginatedQuestionMetaFilteredByTest {
        @Test
        void shouldThrowBadTokenExceptionOnInvalidNonNullTokensWithNonNullPassedFilter() {
            // Given a bad token
            String token = String.valueOf(random.nextInt());
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    eq(token),
                    any())
            ).thenThrow(ExpiredJwtException.class);
            // When calling getPaginatedQuestionMetaFilteredBy with passed set and token set
            // Then wrapped exception should be thrown
            assertThrows(BadTokenException.class, () -> jwtQuestionService.getPaginatedQuestionMetaFilteredBy(
                    Subject.MATHEMATICS_EXTENSION_2,
                    Topic.MATHEMATICS_EXTENSION_2_TOPIC_6,
                    Difficulty.EASY,
                    token,
                    true,
                    10,
                    0
            ));
        }

        @Test
        void shouldDefaultToFirstPageOf10SizedPagesOnNullPaginationParameters() throws ApiException {
            // Given certain list of questions to be returned on certain subject and topic
            Subject subject = Subject.MATHEMATICS_EXTENSION_2;
            Topic topic = Topic.MATHEMATICS_EXTENSION_2_TOPIC_6;
            Difficulty difficulty = Difficulty.EASY;
            Page<Question> questions = new PageImpl<>(new ArrayList<>());
            when(mockedQuestionAttemptService.getPaginatedFilteredQuestions(
                    anyInt(),
                    anyInt(),
                    eq(subject), eq(topic), eq(difficulty),
                    any(), any()
            )).thenReturn(questions);
            // When calling with null pagination parameters
            jwtQuestionService.getPaginatedQuestionMetaFilteredBy(
                    subject,
                    topic,
                    difficulty,
                    null,
                    null,
                    null,
                    null
            );
            // Then 0 page of size 10 should have been requested
            verify(mockedQuestionAttemptService, times(1))
                    .getPaginatedFilteredQuestions(
                            eq(10),
                            eq(0),
                            eq(subject),
                            eq(topic),
                            eq(difficulty),
                            eq(null), eq(null));
        }

        @Test
        void shouldReturnConstructedPaginatedQuestionMeta() throws ApiException {
            // Given a few questions of certain criteria
            Subject subject = Subject.MATHEMATICS_EXTENSION_2;
            Topic topic = Topic.MATHEMATICS_EXTENSION_2_TOPIC_6;
            Difficulty difficulty = Difficulty.EASY;
            QuestionType type = QuestionType.MCQ;
            List<Question> questions = new ArrayList<>();
            List<QuestionMeta> questionMetas = new ArrayList<>();
            for (int __ = 0; __ < 100; ++__) {
                Question question = new Question();
                question.setId(random.nextInt());
                question.setSubject(subject.getId());
                question.setTopic(topic.getId());
                question.setDifficulty(difficulty.getId());
                question.setType(type.getId());
                question.setTitle(String.valueOf(random.nextInt()));
                questions.add(question);

                QuestionMeta questionMeta = new QuestionMeta(
                        question.getId().toString(),
                        question.getTitle(),
                        question.getType(),
                        question.getDifficulty(),
                        question.getTopic().toString(),
                        false
                );
                questionMetas.add(questionMeta);
            }
            Page<Question> questionPage = new PageImpl<>(questions);
            when(mockedQuestionAttemptService.getPaginatedFilteredQuestions(
                    anyInt(),
                    anyInt(),
                    eq(subject), eq(topic), eq(difficulty),
                    any(), any()
            )).thenReturn(questionPage);
            // When calling
            PaginatedQuestionMeta result = jwtQuestionService.getPaginatedQuestionMetaFilteredBy(
                    subject,
                    topic,
                    difficulty,
                    null,
                    null,
                    null,
                    null
            );
            // Then the constructed result should have all the prepared questions
            assertEquals(questionMetas.size(), result.getQuestions().size());
            boolean allMatched = true;
            for (QuestionMeta questionMeta : questionMetas) {
                boolean isSingleMatched = false;
                for (QuestionMeta actual : result.getQuestions()) {
                    if (questionMeta.getQuestionId().equals(actual.getQuestionId())) {
                        isSingleMatched = true;
                        break;
                    }
                }
                if (!isSingleMatched) {
                    allMatched = false;
                    break;
                }
            }
            assertTrue(allMatched);
        }
    }

    @Nested
    class CheckQuestionAttemptTest {
        @Test
        void shouldThrowBadQuestionIdExceptionOnInvalidQuestionIds() {
            assertThrows(BadQuestionIdException.class, () -> jwtQuestionService.checkQuestionAttempt(null, null));
            assertThrows(BadQuestionIdException.class, () -> jwtQuestionService.checkQuestionAttempt(null, ""));
            assertThrows(BadQuestionIdException.class, () -> jwtQuestionService.checkQuestionAttempt(null, "a"));
        }

        @Test
        void shouldThrowNoAttemptExceptionWhenNoAttemptFoundInDB() {
            // Given no attempts for a user on a question
            int userId = random.nextInt();
            int questionId = random.nextInt();
            when(mockedQuestionAttemptService.hasAttemptsOfQuestion(
                    userId,
                    questionId)
            ).thenReturn(false);

            assertThrows(NoQuestionAttemptException.class,
                    () -> jwtQuestionService.checkQuestionAttempt(userId, Integer.toString(questionId)));
        }
    }

    @Nested
    class GetQuestionByIdTest {
        @Test
        void shouldThrowBadQuestionIdExceptionWhenQuestionNotFound() {
            int questionId = random.nextInt();
            when(mockedQuestionAttemptService.getQuestionById(questionId))
                    .thenReturn(null);
            assertThrows(BadQuestionIdException.class, () -> jwtQuestionService.getQuestionById(questionId, null));
        }

        @Test
        void shouldReturnQuestionDetailWithQuestionAttemptedAsFalseWhenAccessTokenNotProvided() throws ApiException {
            int questionId = random.nextInt();
            int difficulty = random.nextInt();
            String title = String.valueOf(random.nextInt());
            int type = random.nextInt();
            String body = String.valueOf(random.nextInt());
            Question question = new Question();
            question.setId(questionId);
            question.setDifficulty(difficulty);
            question.setTitle(title);
            question.setType(type);
            question.setBody(body);

            when(mockedQuestionAttemptService.getQuestionById(questionId))
                    .thenReturn(question);

            QuestionDetail detail = jwtQuestionService.getQuestionById(questionId, null);
            assertEquals(String.valueOf(questionId), detail.getId());
            assertEquals(difficulty, detail.getDifficulty());
            assertEquals(title, detail.getTitle());
            assertEquals(type, detail.getType());
            assertEquals(body, detail.getBody());
            assertFalse(detail.isAttempted());
        }

        @Test
        void shouldReturnQuestionDetailWithQuestionAttemptedAsPreparedWhenAccessTokenProvided() throws ApiException {
            int numQuestions = 100;

            Integer userId = random.nextInt();
            String token = String.valueOf(random.nextInt());
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    token,
                    JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS
            )).thenReturn(userId.toString());

            List<Integer> questionIds = new ArrayList<>();
            List<Integer> difficulties = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            List<Integer> types = new ArrayList<>();
            List<String> bodies = new ArrayList<>();
            List<Boolean> attemptedStatus = new ArrayList<>();
            for (int __ = 0; __ < numQuestions; ++__) {
                int questionId = random.nextInt();
                int difficulty = random.nextInt();
                String title = String.valueOf(random.nextInt());
                int type = random.nextInt();
                String body = String.valueOf(random.nextInt());
                boolean attempted = random.nextBoolean();

                questionIds.add(questionId);
                difficulties.add(difficulty);
                titles.add(title);
                types.add(type);
                bodies.add(body);
                attemptedStatus.add(attempted);

                Question question = new Question();
                question.setId(questionId);
                question.setDifficulty(difficulty);
                question.setTitle(title);
                question.setType(type);
                question.setBody(body);

                when(mockedQuestionAttemptService.getQuestionById(questionId))
                        .thenReturn(question);
                when(mockedQuestionAttemptService.hasAttemptsOfQuestion(
                        userId,
                        questionId
                )).thenReturn(attempted);
            }

            for (int idx = 0; idx < numQuestions; ++idx) {
                int questionId = questionIds.get(idx);
                QuestionDetail detail = jwtQuestionService.getQuestionById(questionId, token);

                assertEquals(String.valueOf(questionId), detail.getId());
                assertEquals(difficulties.get(idx), detail.getDifficulty());
                assertEquals(titles.get(idx), detail.getTitle());
                assertEquals(types.get(idx), detail.getType());
                assertEquals(bodies.get(idx), detail.getBody());
                assertEquals(attemptedStatus.get(idx), detail.isAttempted());
            }
        }

        @Test
        void shouldNotThrowExceptionsOnInvalidTokens() {
            Question question = new Question();
            question.setId(123);
            question.setDifficulty(123);
            question.setType(123);
            question.setTitle("123");
            question.setBody("123");
            when(mockedQuestionAttemptService.getQuestionById(anyInt())).thenReturn(question);
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(any(), any())).thenThrow(ExpiredJwtException.class);

            assertDoesNotThrow(() -> jwtQuestionService.getQuestionById(123, "asdf"));
        }
    }

    @Nested
    class AttemptTest {
        @Test
        void shouldThrowBadQuestionIdWhenQuestionNotFoundById() {
            assertThrows(BadQuestionIdException.class, () -> jwtQuestionService.attempt(123, null, null));
        }

        @Test
        void shouldReturnAnswerCorrectnessButNotUpdateStatsWhenNoUserLoggedIn() throws ApiException {
            String answer = String.valueOf(random.nextInt());
            Question question = new Question();
            question.setAnswer(answer);
            when(mockedQuestionAttemptService.getQuestionById(anyInt())).thenReturn(question);

            boolean actual = jwtQuestionService.attempt(123, answer, null);
            assertTrue(actual);

            actual = jwtQuestionService.attempt(123, "", null);
            assertFalse(actual);

            verify(mockedUserStatsService, never()).incrementSubjectTotalCorrect(anyInt(), anyInt());
            verify(mockedUserStatsService, never()).incrementSubjectAttempt(anyInt(), anyInt());
            verify(mockedUserStatsService, never()).incrementSubjectFirstCorrect(anyInt(), anyInt());
            verify(mockedUserStatsService, never()).incrementTotalCorrect(anyInt());
            verify(mockedUserStatsService, never()).incrementTotalAttempt(anyInt());
        }

        @Test
        void shouldTreatBadTokenAsNoUserLoggedIn() throws ApiException {
            String answer = String.valueOf(random.nextInt());
            Question question = new Question();
            question.setAnswer(answer);
            when(mockedQuestionAttemptService.getQuestionById(anyInt())).thenReturn(question);

            String token = String.valueOf(random.nextInt());
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    token,
                    JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS
            )).thenThrow(ExpiredJwtException.class);

            boolean actual = jwtQuestionService.attempt(123, answer, token);
            assertTrue(actual);

            actual = jwtQuestionService.attempt(123, "", token);
            assertFalse(actual);

            verify(mockedUserStatsService, never()).incrementSubjectTotalCorrect(anyInt(), anyInt());
            verify(mockedUserStatsService, never()).incrementSubjectAttempt(anyInt(), anyInt());
            verify(mockedUserStatsService, never()).incrementSubjectFirstCorrect(anyInt(), anyInt());
            verify(mockedUserStatsService, never()).incrementTotalCorrect(anyInt());
            verify(mockedUserStatsService, never()).incrementTotalAttempt(anyInt());
        }

        @Test
        void shouldIncrementAllStatsOnCorrectFirstAttemptWhenLoggedIn() throws ApiException {
            String answer = String.valueOf(random.nextInt());
            int subject = random.nextInt();
            Question question = new Question();
            question.setAnswer(answer);
            question.setSubject(subject);
            when(mockedQuestionAttemptService.getQuestionById(anyInt())).thenReturn(question);

            String token = String.valueOf(random.nextInt());
            int userId = random.nextInt();
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    token,
                    JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS
            )).thenReturn(Integer.toString(userId));

            // Unset mocks default to return false
            boolean actual = jwtQuestionService.attempt(123, answer, token);
            assertTrue(actual);
            verify(mockedUserStatsService, times(1))
                    .incrementTotalCorrect(userId);
            verify(mockedUserStatsService, times(1))
                    .incrementTotalAttempt(userId);
            verify(mockedUserStatsService, times(1))
                    .incrementSubjectFirstCorrect(userId, subject);
            verify(mockedUserStatsService, times(1))
                    .incrementSubjectAttempt(userId, subject);
            verify(mockedUserStatsService, times(1))
                    .incrementSubjectTotalCorrect(userId, subject);
        }

        @Test
        void shouldOnlyIncrementTotalCorrectOnCorrectAttemptAfterFailedAttemptsWhenLoggedIn() throws ApiException {
            String answer = String.valueOf(random.nextInt());
            int subject = random.nextInt();
            Question question = new Question();
            question.setAnswer(answer);
            question.setSubject(subject);
            when(mockedQuestionAttemptService.getQuestionById(anyInt())).thenReturn(question);

            String token = String.valueOf(random.nextInt());
            int userId = random.nextInt();
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    token,
                    JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS
            )).thenReturn(Integer.toString(userId));

            when(mockedQuestionAttemptService.hasAttemptsOfQuestion(
                    eq(userId),
                    anyInt()
            )).thenReturn(true);

            boolean actual = jwtQuestionService.attempt(123, answer, token);
            assertTrue(actual);
            verify(mockedUserStatsService, times(1))
                    .incrementTotalCorrect(userId);
            verify(mockedUserStatsService, never())
                    .incrementTotalAttempt(userId);
            verify(mockedUserStatsService, never())
                    .incrementSubjectFirstCorrect(userId, subject);
            verify(mockedUserStatsService, never())
                    .incrementSubjectAttempt(userId, subject);
            verify(mockedUserStatsService, times(1))
                    .incrementSubjectTotalCorrect(userId, subject);
        }
    }

    @Nested
    class GetLatestAttemptByQuestionIdTest {
        @Test
        void shouldThrowUnauthenticatedUserExceptionOnInvalidTokens() {
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    anyString(),
                    any()
            )).thenThrow(MalformedJwtException.class);

            assertThrows(UnauthenticatedUserException.class,
                    () -> jwtQuestionService.getLatestAttemptByQuestionId(null, 1));
            assertThrows(UnauthenticatedUserException.class,
                    () -> jwtQuestionService.getLatestAttemptByQuestionId("", 1));
            assertThrows(UnauthenticatedUserException.class,
                    () -> jwtQuestionService.getLatestAttemptByQuestionId("null", 1));
        }

        @Test
        void shouldThrowNoAttemptExceptionWhenNoDataFound() {
            int userId = random.nextInt();
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    anyString(),
                    any()
            )).thenReturn(String.valueOf(userId));
            when(mockedQuestionAttemptService.getQuestionById(anyInt())).thenReturn(Mockito.mock(Question.class));

            assertThrows(NoQuestionAttemptException.class,
                    () -> jwtQuestionService.getLatestAttemptByQuestionId("asdf", 1));
        }

        @Test
        void shouldReturnLastAttemptFetchedFromDatabase() throws ApiException {
            int userId = random.nextInt();
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    anyString(),
                    any()
            )).thenReturn(String.valueOf(userId));

            int questionId = random.nextInt();
            String answer = String.valueOf(random.nextInt());
            boolean isCorrect = random.nextBoolean();
            Attempt attempt = new Attempt();
            attempt.setAnswer(answer);
            attempt.setCorrect(isCorrect);

            when(mockedQuestionAttemptService.getQuestionById(questionId)).thenReturn(Mockito.mock(Question.class));

            when(mockedQuestionAttemptService.getLatestAttemptByQuestionId(
                    userId,
                    questionId
            )).thenReturn(attempt);

            AttemptDetail detail = jwtQuestionService.getLatestAttemptByQuestionId("asdf", questionId);
            assertEquals(answer, detail.getBody());
            assertEquals(isCorrect, detail.isCorrect());
        }
    }
}
