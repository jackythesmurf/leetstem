package au.edu.sydney.elec5619.leetstem.payload.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SignUpRequestRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
