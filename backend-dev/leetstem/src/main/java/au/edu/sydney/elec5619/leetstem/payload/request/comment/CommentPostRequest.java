package au.edu.sydney.elec5619.leetstem.payload.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CommentPostRequest {
    @JsonProperty("question_id")
    private String questionId;

    @JsonProperty("comment_type")
    private int commentType;

    @JsonProperty("comment_body")
    private String commentBody;
}
