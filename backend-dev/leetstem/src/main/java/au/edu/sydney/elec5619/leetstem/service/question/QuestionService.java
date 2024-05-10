package au.edu.sydney.elec5619.leetstem.service.question;

import au.edu.sydney.elec5619.leetstem.constant.Difficulty;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import au.edu.sydney.elec5619.leetstem.dto.AttemptDetail;
import au.edu.sydney.elec5619.leetstem.dto.PaginatedQuestionMeta;
import au.edu.sydney.elec5619.leetstem.dto.QuestionDetail;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;

public interface QuestionService {
    PaginatedQuestionMeta getPaginatedQuestionMetaFilteredBy(Subject subject, Topic topic, Difficulty difficulty,
                                                             String token, Boolean passed, Integer pageSize,
                                                             Integer pageNo) throws ApiException;

    void checkQuestionAttempt(Integer userId, String questionId) throws ApiException;

    QuestionDetail getQuestionById(int questionId, String token) throws ApiException;

    boolean attempt(int questionId, String attemptBody, String token) throws ApiException;

    AttemptDetail getLatestAttemptByQuestionId(String token, int questionId) throws ApiException;
}
