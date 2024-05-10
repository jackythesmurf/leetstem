package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBadgeDTO {
    @JsonProperty("badge_id")
    private String badgeId;

    @JsonProperty("displayed")
    private boolean displayed;

    @JsonProperty("badge_name")
    private String badgeName;

    @JsonProperty("badge_icon")
    private String badgeIcon;
}
