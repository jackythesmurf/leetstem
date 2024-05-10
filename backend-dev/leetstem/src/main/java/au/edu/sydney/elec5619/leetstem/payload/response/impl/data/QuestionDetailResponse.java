package au.edu.sydney.elec5619.leetstem.payload.response.impl.data;

import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionDetailResponse extends BaseResponse {

    @JsonProperty("question_id")
    private String questionId;

    @JsonProperty("attempted")
    private boolean attempted;

    @JsonProperty("question_difficulty")
    private int difficulty;

    @JsonProperty("question_title")
    private String title;

    @JsonProperty("question_type")
    private int type;

    @JsonProperty("question_body")
    @JsonRawValue
    private String body;
}
