package au.edu.sydney.elec5619.leetstem.payload.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PasswordResetRequest {
    @NotNull
    @JsonProperty("new_password")
    private String newPassword;
    @NotNull
    private String token;
}
