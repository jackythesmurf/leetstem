package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("role")
    private int role;

    public UserDTO(String displayName, String avatar, int role) {
        this.displayName = displayName;
        this.avatar = avatar;
        this.role = role;
    }
}
