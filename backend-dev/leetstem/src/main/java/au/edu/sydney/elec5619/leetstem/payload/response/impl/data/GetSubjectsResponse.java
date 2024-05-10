package au.edu.sydney.elec5619.leetstem.payload.response.impl.data;

import au.edu.sydney.elec5619.leetstem.dto.SubjectDetail;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetSubjectsResponse extends BaseResponse {
    @JsonProperty("is_none_selected")
    private boolean isNoneSelected;
    @JsonProperty("subjects")
    private List<SubjectDetail> subjectDetails;
}
