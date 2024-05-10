package au.edu.sydney.elec5619.leetstem.controller;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.UserDTO;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.payload.request.auth.*;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.SimpleResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.user.UserLoginResponse;
import au.edu.sydney.elec5619.leetstem.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@Tag(name = "User authentication", description = "Endpoints of user authentication related functions")
public class UserAuthController {
    private final AuthService jwtAuthService;

    public UserAuthController(AuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }

    @Operation(summary = "Authenticate as a user",
            description = "Get a new access token injected into a cookie with email and password")
    @PostMapping("/signin")
    public UserLoginResponse signIn(@Valid @RequestBody SignInRequest signInRequest,
                                    HttpServletResponse response) throws ApiException {
        // user login and generate token
        String token = jwtAuthService.signIn(signInRequest.getEmail(), signInRequest.getPassword());

        // user details
        UserDTO userDTO = jwtAuthService.getUserDetailsByEmail(signInRequest.getEmail());

        // add cookie
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setPath("/api");
        response.addCookie(cookie);

        return new UserLoginResponse(ErrorCode.ERROR_CODE_OK,
                userDTO.getDisplayName(), userDTO.getAvatar(), userDTO.getRole());
    }

    @Operation(summary = "Attempt to create an account",
            description = "Start a sign up request, and sent to the provided address a token for verification. " +
                    "Known emails won't be reported.")
    @PostMapping("/signup-request")
    public SimpleResponse signUpRequest(@Valid @RequestBody SignUpRequestRequest signUpRequestRequest)
            throws ApiException {
        jwtAuthService.signUpRequest(signUpRequestRequest.getEmail(), signUpRequestRequest.getPassword());
        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "Verify and finalise creation of an account",
            description = "Verifies a previous sign up request, with the token sent out when making a sign up request")
    @PostMapping("/signup-verify")
    public SimpleResponse signUpVerify(@Valid @RequestBody SignUpVerifyRequest signUpVerifyRequest)
            throws ApiException {
        jwtAuthService.signUpVerify(signUpVerifyRequest.getToken());
        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "Get a token to reset password",
            description = "Start a password reset request, and sent to the provided address a token to authorise " +
                    "a password reset. Unknown emails won't be reported.")
    @PostMapping("/passwd-reset-request")
    public SimpleResponse passwdResetRequest(@Valid @RequestBody PasswordResetRequestRequest passwordResetReqRequest)
            throws ApiException {
        jwtAuthService.passwordResetRequest(passwordResetReqRequest.getEmail());
        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "Reset password with a token and new password",
            description = "Reset the password of the user corresponding to the provided password reset request token.")
    @PostMapping("/passwd-reset")
    public SimpleResponse passwdReset(@Valid @RequestBody PasswordResetRequest passwordResetRequest)
            throws ApiException {
        jwtAuthService.passwordReset(passwordResetRequest.getToken(), passwordResetRequest.getNewPassword());
        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "Sign out",
            description = "Deletes the access token from the cookie. " +
                    "The token also goes to a blacklist until they expire")
    @PostMapping("/signout")
    public SimpleResponse signOut(@CookieValue(value = "accessToken", required = false) String accessToken,
                                  HttpServletRequest request, HttpServletResponse response) {
        if (accessToken != null) {
            // invalidate the accessToken cookie by setting max age to 0
            Cookie cookie = new Cookie("accessToken", null);
            cookie.setPath("/api");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            jwtAuthService.signOut(accessToken);
        }

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }
}
