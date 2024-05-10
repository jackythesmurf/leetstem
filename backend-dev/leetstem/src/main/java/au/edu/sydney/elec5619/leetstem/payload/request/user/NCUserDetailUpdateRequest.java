package au.edu.sydney.elec5619.leetstem.payload.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NCUserDetailUpdateRequest {
    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("avatar_id")
    private String avatarId;
}
