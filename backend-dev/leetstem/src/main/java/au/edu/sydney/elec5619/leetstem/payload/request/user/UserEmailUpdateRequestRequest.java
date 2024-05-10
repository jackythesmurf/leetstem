package au.edu.sydney.elec5619.leetstem.payload.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserEmailUpdateRequestRequest {
    @JsonProperty("new_email")
    private String newEmail;
}
