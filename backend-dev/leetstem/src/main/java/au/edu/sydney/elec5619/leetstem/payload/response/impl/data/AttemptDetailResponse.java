package au.edu.sydney.elec5619.leetstem.payload.response.impl.data;

import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AttemptDetailResponse extends BaseResponse {
    @JsonProperty("attempt_body")
    private String body;
    @JsonProperty("was_correct")
    private boolean wasCorrect;
}
