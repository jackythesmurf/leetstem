package au.edu.sydney.elec5619.leetstem.service.auth;

import au.edu.sydney.elec5619.leetstem.dto.UserDTO;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;

public interface AuthService {
    String signIn(String email, String password) throws ApiException;

    void signOut(String token);

    void signUpRequest(String email, String password) throws ApiException;

    void signUpVerify(String token) throws ApiException;

    void passwordResetRequest(String email) throws ApiException;

    void passwordReset(String token, String password) throws ApiException;

    UserDTO getUserDetailsByEmail(String email);
}
