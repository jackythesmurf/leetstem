package au.edu.sydney.elec5619.leetstem.controller;


import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.PaginatedComment;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.payload.request.comment.CommentDeleteRequest;
import au.edu.sydney.elec5619.leetstem.payload.request.comment.CommentEndorseRequest;
import au.edu.sydney.elec5619.leetstem.payload.request.comment.CommentPostRequest;
import au.edu.sydney.elec5619.leetstem.payload.request.comment.CommentVoteRequest;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.SimpleResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.comment.CommentListResponse;
import au.edu.sydney.elec5619.leetstem.service.comment.CommentService;
import au.edu.sydney.elec5619.leetstem.service.question.QuestionService;
import au.edu.sydney.elec5619.leetstem.service.user.UserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/data/comments")
@RestController
@Tag(name = "4. Comments", description = "Endpoints of comment details and actions")
public class CommentController {

    private final CommentService commentService;
    private final UserDetailService userDetailService;
    private final QuestionService questionService;

    public CommentController(CommentService commentService,
                             UserDetailService userDetailService,
                             QuestionService questionService) {
        this.commentService = commentService;
        this.userDetailService = userDetailService;
        this.questionService = questionService;
    }

    @Operation(summary = "4.1 List comments of a question",
            description = "")
    @GetMapping("/list")
    public CommentListResponse listComments(@CookieValue(value = "accessToken", required = false) String accessToken,
                                              @RequestParam(name = "question_id") String questionId,
                                              @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
                                              @RequestParam(name = "page_no", defaultValue = "0") int pageNo)
            throws ApiException {
        // for user with cookie
        Integer userId = -1;
        if (accessToken != null) {
            // validate token
            userId = userDetailService.getUserIdByToken(accessToken);
            // validate attempt
            questionService.checkQuestionAttempt(userId, questionId);
        }

        // fetch data
        PaginatedComment paginatedComment = commentService.getCommentsByQuestion(userId, questionId, pageSize, pageNo);

        return new CommentListResponse(ErrorCode.ERROR_CODE_OK, paginatedComment);
    }

    @Operation(summary = "4.2 Post a comment",
            description = "")
    @PostMapping("/post")
    public SimpleResponse postComment(@CookieValue(value = "accessToken", required = false) String accessToken,
                                         @Valid @RequestBody CommentPostRequest commentPostRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailService.getUserIdByToken(accessToken);

        // get request data
        String questionId = commentPostRequest.getQuestionId();
        int commentType = commentPostRequest.getCommentType();
        String commentBody = commentPostRequest.getCommentBody();

        // validate attempt
        questionService.checkQuestionAttempt(userId, questionId);

        // post comment
        commentService.postComment(userId, questionId, commentType, commentBody);

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "4.3 Vote a comment",
            description = "")
    @PostMapping("/vote")
    public SimpleResponse voteComment(@CookieValue(value = "accessToken", required = false) String accessToken,
                                      @Valid @RequestBody CommentVoteRequest commentVoteRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailService.getUserIdByToken(accessToken);

        // get request data
        String commentId = commentVoteRequest.getCommentId();
        int direction = commentVoteRequest.getDirection();

        // vote comment
        commentService.voteComment(userId, commentId, direction);

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "4.4 Endorse a comment",
            description = "")
    @PostMapping("/endorse")
    public SimpleResponse endorseComment(@CookieValue(value = "accessToken", required = false) String accessToken,
                                        @Valid @RequestBody CommentEndorseRequest commentEndorseRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailService.getUserIdByToken(accessToken);

        // check privilege
        userDetailService.checkPrivilege(userId);

        // get request data
        String commentId = commentEndorseRequest.getCommentId();
        boolean endorsed = commentEndorseRequest.isEndorsed();

        // endorse comment
        commentService.endorseComment(commentId, endorsed);

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "4.5 Delete a comment",
            description = "")
    @PostMapping("/delete")
    public SimpleResponse deleteComment(@CookieValue(value = "accessToken", required = false) String accessToken,
                                        @Valid @RequestBody CommentDeleteRequest commentDeleteRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailService.getUserIdByToken(accessToken);

        // check privilege
        userDetailService.checkPrivilege(userId);

        // get request data
        String commentId = commentDeleteRequest.getCommentId();

        // delete comment
        commentService.deleteComment(commentId);

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }


}
