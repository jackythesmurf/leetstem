package au.edu.sydney.elec5619.leetstem.payload.response.impl.user;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSubjectUpdateResponse extends BaseResponse {

    @JsonProperty("subject_id")
    private String subjectId;

    @JsonProperty("is_selected")
    private boolean isSelected;


    public UserSubjectUpdateResponse(ErrorCode errorCode, String subjectId, boolean isSelected) {
        this.errorCode = errorCode.getCode();
        this.subjectId = subjectId;
        this.isSelected = isSelected;
    }
}
