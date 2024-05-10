package au.edu.sydney.elec5619.leetstem.service.db;

import au.edu.sydney.elec5619.leetstem.dto.PaginatedComment;
import au.edu.sydney.elec5619.leetstem.entity.Comment;

public interface CommentDataService {
    PaginatedComment getCommentsByQuestionId(int userId, int questionId, int pageSize, int pageNo);

    void addComment(Integer userId, int questionIdInt, int commentType, String commentBody);

    Comment getCommentById(int commentIdInt);

    void voteComment(Integer userId, Comment comment, int direction);

    void endorseComment(Comment comment, boolean endorsed);

    void deleteComment(Comment comment);
}
