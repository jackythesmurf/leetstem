package au.edu.sydney.elec5619.leetstem.service.comment;

import au.edu.sydney.elec5619.leetstem.dto.PaginatedComment;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;

public interface CommentService {
    PaginatedComment getCommentsByQuestion(int userId, String questionId, Integer pageSize, Integer pageNo) throws ApiException;

    void postComment(Integer userId, String questionId, int commentType, String commentBody) throws ApiException;

    void voteComment(Integer userId, String commentId, int direction) throws ApiException;

    void endorseComment(String commentId, boolean endorsed) throws ApiException;

    void deleteComment(String commentId) throws ApiException;
}
