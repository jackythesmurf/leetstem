package au.edu.sydney.elec5619.leetstem.payload.request.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AttemptSubmissionRequest {
    @JsonProperty("question_id")
    private String questionId;

    @JsonProperty("attempt_body")
    private String body;
}
