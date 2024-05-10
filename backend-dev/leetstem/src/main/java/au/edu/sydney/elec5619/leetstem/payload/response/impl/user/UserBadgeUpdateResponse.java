package au.edu.sydney.elec5619.leetstem.payload.response.impl.user;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserBadgeUpdateResponse extends BaseResponse {

    @JsonProperty("badge_id")
    private String badgeId;

    @JsonProperty("is_displayed")
    private Boolean isDisplayed;


    public UserBadgeUpdateResponse(ErrorCode errorCode, String badgeId, Boolean isDisplayed) {
        this.errorCode = errorCode.getCode();
        this.badgeId = badgeId;
        this.isDisplayed = isDisplayed;
    }
}
