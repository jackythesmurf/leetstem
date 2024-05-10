package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubjectRankingUserBadge {

    @JsonProperty("badge_id")
    private String badgeId;

    @JsonProperty("badge_name")
    private String badgeName;

    @JsonProperty("badge_icon")
    private String badgeIcon;

    public SubjectRankingUserBadge() {

    }
}
