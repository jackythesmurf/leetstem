package au.edu.sydney.elec5619.leetstem.payload.response.impl.user;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLoginResponse extends BaseResponse {
    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("role")
    private int role;


    public UserLoginResponse(ErrorCode errorCode, String displayName, String avatar, int role) {
        this.errorCode = errorCode.getCode();
        this.displayName = displayName;
        this.avatar = avatar;
        this.role = role;
    }
}
