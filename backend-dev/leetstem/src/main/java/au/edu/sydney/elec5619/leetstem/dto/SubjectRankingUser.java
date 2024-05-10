package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubjectRankingUser {

    @JsonProperty("rank")
    private int rank;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("user_image")
    private String userImage;

    @JsonProperty("points")
    private int points;

    @JsonProperty("badges")
    private List<SubjectRankingUserBadge> badges;

    public SubjectRankingUser() {

    }
}
