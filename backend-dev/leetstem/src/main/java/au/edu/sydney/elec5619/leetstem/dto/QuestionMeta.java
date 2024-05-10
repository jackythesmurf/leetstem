package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionMeta {
    @JsonProperty("question_id")
    private String questionId;

    @JsonProperty("question_title")
    private String questionTitle;

    @JsonProperty("question_type")
    private int questionType;

    @JsonProperty("question_difficulty")
    private int difficulty;

    @JsonProperty("question_topic")
    private String topic;

    @JsonProperty("passed")
    private boolean passed;
}
