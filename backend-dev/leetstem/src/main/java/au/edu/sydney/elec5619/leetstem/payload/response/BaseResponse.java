package au.edu.sydney.elec5619.leetstem.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class BaseResponse {
    @JsonProperty("error_code")
    protected int errorCode;
}
