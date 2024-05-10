package au.edu.sydney.elec5619.leetstem.service.db;

import au.edu.sydney.elec5619.leetstem.constant.Difficulty;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import au.edu.sydney.elec5619.leetstem.entity.Attempt;
import au.edu.sydney.elec5619.leetstem.entity.Question;
import org.springframework.data.domain.Page;

public interface QuestionAttemptService {
    Page<Question> getPaginatedFilteredQuestions(int pageSize, int pageNo, Subject subject, Topic topic,
                                                 Difficulty difficulty, Integer userId, Boolean passed);

    boolean hasAttemptsOfQuestion(Integer userId, int questionId);

    Question getQuestionById(int questionId);

    void insertNewAttempt(int userId, int questionId, String attemptBody, boolean isCorrect);

    boolean hasCorrectAttemptsOfQuestion(int userId, int questionId);

    Attempt getLatestAttemptByQuestionId(int userId, int questionId);
}
