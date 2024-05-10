package au.edu.sydney.elec5619.leetstem.exception.handler;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.exception.impl.*;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.SimpleResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<SimpleResponse> handleAuthenticationFailedException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_AUTHENTICATION_FAILED), HttpStatus.OK);
    }

    @ExceptionHandler(MalformedEmailException.class)
    public ResponseEntity<SimpleResponse> handleMalformedEmailException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_MALFORMED_EMAIL), HttpStatus.OK);
    }

    @ExceptionHandler(EmptyPasswordException.class)
    public ResponseEntity<SimpleResponse> handleEmptyPasswordException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_EMPTY_PASSWORD), HttpStatus.OK);
    }

    @ExceptionHandler(BadTokenException.class)
    public ResponseEntity<SimpleResponse> handleBadTokenException(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setPath("/api");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_TOKEN), HttpStatus.OK);
    }

    @ExceptionHandler(BadDifficultyException.class)
    public ResponseEntity<SimpleResponse> handleBadDifficultyException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_DIFFICULTY), HttpStatus.OK);
    }

    @ExceptionHandler(BadQuestionTypeException.class)
    public ResponseEntity<SimpleResponse> handleBadQuestionTypeException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_QUESTION_TYPE), HttpStatus.OK);
    }

    @ExceptionHandler(BadSubjectIdException.class)
    public ResponseEntity<SimpleResponse> handleBadSubjectIdException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_SUBJECT_ID), HttpStatus.OK);
    }

    @ExceptionHandler(BadTopicIdException.class)
    public ResponseEntity<SimpleResponse> handleBadTopicIdException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_TOPIC_ID), HttpStatus.OK);
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<SimpleResponse> handleUnauthenticatedUserException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_UNAUTHENTICATED_USER), HttpStatus.OK);
    }

    @ExceptionHandler(TooMuchRequestsException.class)
    public ResponseEntity<SimpleResponse> handleTooMuchRequestsException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_TOO_MUCH_REQUESTS), HttpStatus.OK);
    }

    @ExceptionHandler(BadContentException.class)
    public ResponseEntity<SimpleResponse> handleBadContentException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_CONTENT), HttpStatus.OK);
    }

    @ExceptionHandler(BadBadgeIdException.class)
    public ResponseEntity<SimpleResponse> handleBadBadgeIdException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_BADGE_ID), HttpStatus.OK);
    }

    @ExceptionHandler(TooMuchDisplayedBadgesException.class)
    public ResponseEntity<SimpleResponse> handleTooMuchDisplayedBadgesException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_TOO_MUCH_DISPLAYED_BADGES), HttpStatus.OK);
    }

    @ExceptionHandler(NoQuestionAttemptException.class)
    public ResponseEntity<SimpleResponse> handleNoQuestionAttemptException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_NO_ATTEMPT), HttpStatus.OK);
    }

    @ExceptionHandler(BadPaginationParametersException.class)
    public ResponseEntity<SimpleResponse> handleBadPaginationParametersException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_PAGINATION_PARAMETERS), HttpStatus.OK);
    }

    @ExceptionHandler(BadCommentTypeException.class)
    public ResponseEntity<SimpleResponse> handleBadCommentTypeException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_COMMENT_TYPE), HttpStatus.OK);
    }

    @ExceptionHandler(BadCommentIdException.class)
    public ResponseEntity<SimpleResponse> handleBadCommentIdException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_COMMENT_ID), HttpStatus.OK);
    }

    @ExceptionHandler(NonPrivilegedUserException.class)
    public ResponseEntity<SimpleResponse> handleNonPrivilegedUserException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_NO_PRIVILEGE), HttpStatus.OK);
    }

    @ExceptionHandler(BadQuestionIdException.class)
    public ResponseEntity<SimpleResponse> handleBadQuestionIdException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_BAD_QUESTION_ID), HttpStatus.OK);
    }

    @ExceptionHandler(NullAttemptBodyException.class)
    public ResponseEntity<SimpleResponse> handleNullAttemptBodyException() {
        return new ResponseEntity<>(new SimpleResponse(ErrorCode.ERROR_CODE_NULL_ATTEMPT_BODY), HttpStatus.OK);
    }
}
