package au.edu.sydney.elec5619.leetstem.payload.response.impl.user;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserBadgesResponse extends BaseResponse {

    @JsonProperty("badges")
    private List<UserBadgeDTO> badges;

    public UserBadgesResponse(ErrorCode errorCode, List<UserBadgeDTO> badges) {
        this.errorCode = errorCode.getCode();
        this.badges = badges;
    }

}

