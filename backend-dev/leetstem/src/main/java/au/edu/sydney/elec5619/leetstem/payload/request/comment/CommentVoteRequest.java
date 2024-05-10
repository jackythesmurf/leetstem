package au.edu.sydney.elec5619.leetstem.payload.request.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CommentVoteRequest {
    @JsonProperty("comment_id")
    private String commentId;

    @JsonProperty("direction")
    private int direction;
}
