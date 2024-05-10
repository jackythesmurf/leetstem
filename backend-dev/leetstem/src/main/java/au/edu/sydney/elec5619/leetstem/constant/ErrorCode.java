package au.edu.sydney.elec5619.leetstem.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ERROR_CODE_OK(0),
    //////////////////// Auth section ////////////////////
    ERROR_CODE_AUTHENTICATION_FAILED(1001),
    ERROR_CODE_MALFORMED_EMAIL(1002),
    ERROR_CODE_EMPTY_PASSWORD(1003),
    ERROR_CODE_UNAUTHENTICATED_USER(1005),
    ERROR_CODE_NO_PRIVILEGE(1006),

    //////////////////// Question section ////////////////////
    ERROR_CODE_BAD_SUBJECT_ID(3001),
    ERROR_CODE_BAD_TOPIC_ID(3002),
    ERROR_CODE_BAD_DIFFICULTY(3003),
    ERROR_CODE_NO_ATTEMPT(3004),
    ERROR_CODE_BAD_QUESTION_TYPE(3005),
    ERROR_CODE_BAD_QUESTION_ID(3006),
    ERROR_CODE_NULL_ATTEMPT_BODY(3007),

    //////////////////// Comment section ////////////////////
    ERROR_CODE_BAD_COMMENT_ID(4001),
    ERROR_CODE_BAD_COMMENT_TYPE(4002),

    //////////////////// User section ////////////////////
    ERROR_CODE_BAD_BADGE_ID(5001),
    ERROR_CODE_TOO_MUCH_DISPLAYED_BADGES(5002),

    //////////////////// General section ////////////////////
    ERROR_CODE_BAD_TOKEN(9002),
    ERROR_CODE_BAD_PAGINATION_PARAMETERS(9003),
    ERROR_CODE_TOO_MUCH_REQUESTS(9004),
    ERROR_CODE_BAD_CONTENT(9005);

    private final int code;
}
