package au.edu.sydney.elec5619.leetstem.payload.response.impl.data;

import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AttemptSubmissionResponse extends BaseResponse {
    @JsonProperty("passed")
    private boolean passed;
}
