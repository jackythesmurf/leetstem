package au.edu.sydney.elec5619.leetstem.service.comment.impl;


import au.edu.sydney.elec5619.leetstem.entity.Comment;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.*;
import au.edu.sydney.elec5619.leetstem.service.db.CommentDataService;
import au.edu.sydney.elec5619.leetstem.service.profanity.ProfanityCheckService;
import au.edu.sydney.elec5619.leetstem.service.question.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentServiceTest {

    private CommentServiceFacade commentServiceFacade;

    private QuestionService questionServiceMocked;
    private CommentDataService commentDataServiceMocked;
    private ProfanityCheckService purgomalumProfanityCheckServiceMocked;

    @BeforeEach
    public void setup() {
        // mocks
        questionServiceMocked = Mockito.mock(QuestionService.class);
        commentDataServiceMocked = Mockito.mock(CommentDataService.class);
        purgomalumProfanityCheckServiceMocked = Mockito.mock(ProfanityCheckService.class);

        // setup
        commentServiceFacade = new CommentServiceFacade(questionServiceMocked,
                commentDataServiceMocked, purgomalumProfanityCheckServiceMocked);
    }

    @Nested
    class GetCommentsTest {
        @Test
        public void exceptionTest() {
            // bad pageSize/pageNo
            assertThrows(BadPaginationParametersException.class,
                    () -> commentServiceFacade.getCommentsByQuestion(1,"1", -1, 0));
            assertThrows(BadPaginationParametersException.class,
                    () -> commentServiceFacade.getCommentsByQuestion(1,"1", 0, -1));
            assertThrows(BadPaginationParametersException.class,
                    () -> commentServiceFacade.getCommentsByQuestion(1,"1", -1, -1));

            // bad questionId
            assertThrows(BadQuestionIdException.class,
                    () -> commentServiceFacade.getCommentsByQuestion(1,"not number", 1, 1));
        }

        @Test
        public void normalTest() throws ApiException {
            commentServiceFacade.getCommentsByQuestion(1,"1", 1, 1);
            verify(commentDataServiceMocked, times(1)).getCommentsByQuestionId(1,1, 1, 1);
        }
    }

    @Nested
    class PostCommentTest {
        @Test
        public void exceptionTest() throws ApiException {
            // bad comment type
            assertThrows(BadCommentTypeException.class,
                    () -> commentServiceFacade.postComment(1, "1", 2, "test"));

            // bad question id
            assertThrows(BadQuestionIdException.class,
                    () -> commentServiceFacade.postComment(1, "not number", 0, "test"));

            // bad profanity
            String badWords = "bad words";
            when(purgomalumProfanityCheckServiceMocked.containProfanity(eq(badWords))).thenReturn(true);
            assertThrows(BadContentException.class,
                    () -> commentServiceFacade.postComment(1, "1", 0, badWords));

            // update frequency exception
            commentServiceFacade.postComment(1, "1", 0, "test1");
            assertThrows(TooMuchRequestsException.class,
                    () -> commentServiceFacade.postComment(1, "1", 1, "test2"));
        }

        @Test
        public void normalTest() throws InterruptedException, ApiException {
            String goodWords = "good words";
            when(purgomalumProfanityCheckServiceMocked.containProfanity(eq(goodWords))).thenReturn(false);

            // 1st request
            commentServiceFacade.postComment(1, "1", 1, goodWords);
            verify(commentDataServiceMocked, times(1))
                    .addComment(1, 1,1, goodWords);

            Thread.sleep(60_000);

            // 2nd request
            commentServiceFacade.postComment(1, "2", 0, goodWords);
            verify(commentDataServiceMocked, times(1))
                    .addComment(1, 2,0, goodWords);
        }
    }

    @Nested
    class VoteCommentTest {
        @Test
        public void exceptionTest() {
            // bad comment id
            assertThrows(BadCommentIdException.class,
                    () -> commentServiceFacade.voteComment(1, "not number", 1));

            // comment existence
            when(commentDataServiceMocked.getCommentById(eq(1))).thenReturn(null);
            assertThrows(BadCommentIdException.class,
                    () -> commentServiceFacade.voteComment(1, "1", 1));
        }

        @Test
        public void normalTest() throws ApiException {
            Comment comment = new Comment();
            comment.setQuestionId(1);
            when(commentDataServiceMocked.getCommentById(eq(1))).thenReturn(comment);

            commentServiceFacade.voteComment(1, "1", 1);
            verify(questionServiceMocked, times(1)).checkQuestionAttempt(1, "1");
            verify(commentDataServiceMocked, times(1)).voteComment(1, comment, 1);
        }
    }

    @Nested
    class EndorseAndDeleteCommentTest {
        @Test
        public void exceptionTest() {
            // bad comment id
            assertThrows(BadCommentIdException.class,
                    () -> commentServiceFacade.endorseComment("not number", true));
            assertThrows(BadCommentIdException.class,
                    () -> commentServiceFacade.deleteComment("not number"));

            // comment existence
            when(commentDataServiceMocked.getCommentById(eq(1))).thenReturn(null);
            assertThrows(BadCommentIdException.class,
                    () -> commentServiceFacade.endorseComment("1", true));
            assertThrows(BadCommentIdException.class,
                    () -> commentServiceFacade.deleteComment("1"));
        }

        @Test
        public void normalTest() throws ApiException {
            Comment comment = new Comment();
            when(commentDataServiceMocked.getCommentById(eq(1))).thenReturn(comment);

            commentServiceFacade.endorseComment("1", true);
            verify(commentDataServiceMocked, times(1)).endorseComment(comment, true);

            commentServiceFacade.deleteComment("1");
            verify(commentDataServiceMocked, times(1)).deleteComment(comment);
        }
    }

}
