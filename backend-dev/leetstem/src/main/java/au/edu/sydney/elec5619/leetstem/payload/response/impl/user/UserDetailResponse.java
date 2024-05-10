package au.edu.sydney.elec5619.leetstem.payload.response.impl.user;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserDetailResponse extends BaseResponse {

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("email")
    private String email;

    @JsonProperty("badges")
    private List<UserBadgeDTO> badges;

    public UserDetailResponse(ErrorCode errorCode, String displayName, String avatar, String email, List<UserBadgeDTO> badges) {
        this.errorCode = errorCode.getCode();
        this.displayName = displayName;
        this.avatar = avatar;
        this.email = email;
        this.badges = badges;
    }
}
