package au.edu.sydney.elec5619.leetstem.payload.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserBadgeUpdateRequest {
    @JsonProperty("badge_id")
    private String badgeId;

    @JsonProperty("is_displayed")
    private Boolean isDisplayed;
}
