package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubjectRankingDTO {

    @JsonProperty("subject_id")
    private String subjectId;

    @JsonProperty("current_user_rank")
    private int currentUserRank;

    @JsonProperty("current_user_image")
    private String currentUserImage;

    @JsonProperty("current_user_points")
    private int currentUserPoints;

    @JsonProperty("current_user_display_name")
    private String currentUserDisplayName;

    @JsonProperty("users")
    private List<SubjectRankingUser> users;

    public SubjectRankingDTO() {

    }
}
