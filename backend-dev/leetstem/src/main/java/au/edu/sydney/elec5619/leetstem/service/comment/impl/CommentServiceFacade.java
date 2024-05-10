package au.edu.sydney.elec5619.leetstem.service.comment.impl;

import au.edu.sydney.elec5619.leetstem.dto.PaginatedComment;
import au.edu.sydney.elec5619.leetstem.entity.Comment;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.*;
import au.edu.sydney.elec5619.leetstem.service.comment.CommentService;
import au.edu.sydney.elec5619.leetstem.service.db.CommentDataService;
import au.edu.sydney.elec5619.leetstem.service.profanity.ProfanityCheckService;
import au.edu.sydney.elec5619.leetstem.service.question.QuestionService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommentServiceFacade implements CommentService {

    private final QuestionService questionService;
    private final CommentDataService commentDataService;
    private final ProfanityCheckService purgomalumProfanityCheckService;

    private final ConcurrentHashMap<Integer, Long> lastCommentPostTimestamps = new ConcurrentHashMap<>();

    public CommentServiceFacade(QuestionService questionService, CommentDataService commentDataService,
                                ProfanityCheckService purgomalumProfanityCheckService) {
        this.questionService = questionService;
        this.commentDataService = commentDataService;
        this.purgomalumProfanityCheckService = purgomalumProfanityCheckService;
    }

    @Override
    public PaginatedComment getCommentsByQuestion(int userId, String questionId, Integer pageSize, Integer pageNo) throws ApiException {
        // parameter check
        if (pageSize < 0 || pageNo < 0) {
            throw new BadPaginationParametersException();
        }

        // fetch comments
        int questionIdInt;
        try {
            questionIdInt = Integer.parseInt(questionId);
        } catch (NumberFormatException e) {
            throw new BadQuestionIdException();
        }

        return commentDataService.getCommentsByQuestionId(userId, questionIdInt, pageSize, pageNo);
    }

    @Override
    public void postComment(Integer userId, String questionId, int commentType, String commentBody)
            throws ApiException {
        // comment type check
        if (commentType != 0 && commentType != 1) {
            throw new BadCommentTypeException();
        }

        // post period check
        Long lastUpdated = lastCommentPostTimestamps.get(userId);
        if (lastUpdated != null) {
            long differenceInMillis = new Date().getTime() - lastUpdated;
            if (differenceInMillis < 60_000) {
                throw new TooMuchRequestsException();
            }
        }

        // profanity check
        if (purgomalumProfanityCheckService.containProfanity(commentBody)) {
            throw new BadContentException();
        }

        // check question format
        int questionIdInt;
        try {
            questionIdInt = Integer.parseInt(questionId);
        } catch (NumberFormatException e) {
            throw new BadQuestionIdException();
        }

        // add comment
        commentDataService.addComment(userId, questionIdInt, commentType, commentBody);

        lastCommentPostTimestamps.put(userId, new Date().getTime());
    }

    @Override
    public void voteComment(Integer userId, String commentId, int direction) throws ApiException {
        // check comment existence
        int commentIdInt;
        try {
            commentIdInt = Integer.parseInt(commentId);
        } catch (NumberFormatException e) {
            throw new BadCommentIdException();
        }

        Comment comment = commentDataService.getCommentById(commentIdInt);
        if (comment == null) {
            throw new BadCommentIdException();
        }

        // check attempt
        String questionId = comment.getQuestionId().toString();
        questionService.checkQuestionAttempt(userId, questionId);

        // vote comment
        commentDataService.voteComment(userId, comment, direction);
    }

    @Override
    public void endorseComment(String commentId, boolean endorsed) throws ApiException {
        // check comment existence
        int commentIdInt;
        try {
            commentIdInt = Integer.parseInt(commentId);
        } catch (NumberFormatException e) {
            throw new BadCommentIdException();
        }

        Comment comment = commentDataService.getCommentById(commentIdInt);
        if (comment == null) {
            throw new BadCommentIdException();
        }

        // endorse comment
        commentDataService.endorseComment(comment, endorsed);
    }

    @Override
    public void deleteComment(String commentId) throws ApiException {
        // check comment existence
        int commentIdInt;
        try {
            commentIdInt = Integer.parseInt(commentId);
        } catch (NumberFormatException e) {
            throw new BadCommentIdException();
        }

        Comment comment = commentDataService.getCommentById(commentIdInt);
        if (comment == null) {
            throw new BadCommentIdException();
        }

        // delete comment
        commentDataService.deleteComment(comment);
    }
}
