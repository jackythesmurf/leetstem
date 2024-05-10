package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    @JsonProperty("comment_id")
    private String commentId;

    @JsonProperty("commenter_id")
    private String commenterId;

    @JsonProperty("commenter_display_name")
    private String commenterDisplayName;

    @JsonProperty("commenter_avatar")
    private String commenterAvatar;

    @JsonProperty("is_endorsed")
    private boolean isEndorsed;

    @JsonProperty("comment_type")
    private int commentType;

    @JsonProperty("comment_body")
    private String commentBody;

    @JsonProperty("commented_at")
    private long commentedAt;

    @JsonProperty("likes")
    private int likes;

//    @JsonProperty("dislikes")
//    private int dislikes;

    @JsonProperty("is_liked")
    private boolean isLiked;
}
