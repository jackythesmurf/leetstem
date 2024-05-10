package au.edu.sydney.elec5619.leetstem.payload.response.impl;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;

public class SimpleResponse extends BaseResponse {
    public SimpleResponse(ErrorCode errorCode) {
        this.errorCode = errorCode.getCode();
    }
}
