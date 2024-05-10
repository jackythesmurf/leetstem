package au.edu.sydney.elec5619.leetstem.service.auth.impl;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.dto.UserDTO;
import au.edu.sydney.elec5619.leetstem.entity.PendingUser;
import au.edu.sydney.elec5619.leetstem.entity.User;
import au.edu.sydney.elec5619.leetstem.entity.UserAuth;
import au.edu.sydney.elec5619.leetstem.entity.UserRole;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.AuthenticationFailedException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadTokenException;
import au.edu.sydney.elec5619.leetstem.exception.impl.EmptyPasswordException;
import au.edu.sydney.elec5619.leetstem.exception.impl.MalformedEmailException;
import au.edu.sydney.elec5619.leetstem.service.auth.AuthService;
import au.edu.sydney.elec5619.leetstem.service.db.PendingUserService;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import au.edu.sydney.elec5619.leetstem.service.email.EmailService;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

import static java.lang.Math.abs;

@Service
public class JwtAuthService implements AuthService {
    private final StatefulJwtService simpleStatefulJwtService;
    private final UserService jpaUserService;
    private final PendingUserService jpaPendingUserService;
    private final EmailService asyncJavaMailEmailService;
    private final PasswordEncoder bcryptPasswordEncoder;
    private final long accessExpirationMillis;
    private final long signupRequestExpirationMillis;
    private final long passwordResetRequestExpirationMillis;
    private static final String[] avatars = {
            "./images/avatars/1.png",
            "./images/avatars/2.png",
            "./images/avatars/3.png",
            "./images/avatars/4.png",
            "./images/avatars/5.png",
            "./images/avatars/6.png",
            "./images/avatars/7.png",
            "./images/avatars/8.png",
            "./images/avatars/9.png",
            "./images/avatars/10.png"
    };
    private final Random random;

    public JwtAuthService(StatefulJwtService simpleStatefulJwtService,
                          UserService jpaUserService,
                          EmailService asyncJavaMailEmailService,
                          PasswordEncoder bcryptPasswordEncoder,
                          PendingUserService jpaPendingUserService,
                          @Value("${leetstem.expiration.auth}") long accessExpirationMillis,
                          @Value("${leetstem.expiration.signUpRequest}") long signupRequestExpirationMillis,
                          @Value("${leetstem.expiration.passwordResetRequest}") long passwordResetRequestExpirationMillis) {
        this.simpleStatefulJwtService = simpleStatefulJwtService;

        this.jpaUserService = jpaUserService;
        this.asyncJavaMailEmailService = asyncJavaMailEmailService;
        this.jpaPendingUserService = jpaPendingUserService;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;

        this.accessExpirationMillis = accessExpirationMillis;
        this.signupRequestExpirationMillis = signupRequestExpirationMillis;
        this.passwordResetRequestExpirationMillis = passwordResetRequestExpirationMillis;
        this.random = new Random();
    }

    @Override
    public String signIn(String email, String password) throws ApiException {
        // Parameter validation
        if (null == email || !EmailValidator.getInstance().isValid(email)) {
            throw new MalformedEmailException();
        }
        if (null == password || password.isEmpty()) {
            throw new EmptyPasswordException();
        }

        // User auth matching
        UserAuth userAuth = jpaUserService.getUserAuthByEmail(email);
        if (null == userAuth) {
            throw new AuthenticationFailedException();
        }

        if (!bcryptPasswordEncoder.matches(password, userAuth.getDigest())) {
            throw new AuthenticationFailedException();
        }
        return simpleStatefulJwtService.generateToken(
                userAuth.getUserId().toString(),
                JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS,
                accessExpirationMillis);
    }

    @Override
    public void signOut(String token) {
        simpleStatefulJwtService.invalidateToken(token);
    }

    @Override
    public void signUpRequest(String email, String password) throws ApiException {
        // Parameter validation
        if (null == email || !EmailValidator.getInstance().isValid(email)) {
            throw new MalformedEmailException();
        }
        if (null == password || password.isEmpty()) {
            throw new EmptyPasswordException();
        }
        // Email existence check
        if (null != jpaUserService.getUserAuthByEmail(email)) {
            return;
        }
        // Hash password and save to pending user
        String digest = bcryptPasswordEncoder.encode(password);
        PendingUser pendingUser = jpaPendingUserService.insertPendingUser(email, digest);
        // Generate token that binds with this pending user
        String token = simpleStatefulJwtService.generateToken(
                pendingUser.getId().toString(),
                JwtAuthorisedAction.JWT_AUTHORISED_ACTION_SIGN_UP,
                signupRequestExpirationMillis);
        asyncJavaMailEmailService.sendSignUpVerificationEmail(token, email);
    }

    @Override
    public void signUpVerify(String token) throws ApiException {
        String pendingUserId;
        try {
            synchronized (this) {
                pendingUserId = simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(token,
                        JwtAuthorisedAction.JWT_AUTHORISED_ACTION_SIGN_UP);
                if (null == pendingUserId) {
                    throw new BadTokenException();
                }
                simpleStatefulJwtService.invalidateToken(token);
            }
        } catch (UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException |
                 ExpiredJwtException |
                 IllegalArgumentException e) {
            throw new BadTokenException();
        }

        PendingUser pendingUser = jpaPendingUserService.getPendingUserById(Integer.parseInt(pendingUserId));
        // TODO (Yiming): Replace with better strategies to get a random initial display name
        String displayName = "user" + new Random().nextInt(100000, 999999);
        String avatar = avatars[abs(random.nextInt()) % avatars.length];
        jpaUserService.createUser(displayName, avatar, pendingUser.getEmail(), pendingUser.getDigest());
        // Remove the pending user record
        jpaPendingUserService.removePendingUserById(pendingUser.getId());
    }

    @Override
    public void passwordResetRequest(String email) throws ApiException {
        // Parameter validation
        if (null == email || !EmailValidator.getInstance().isValid(email)) {
            throw new MalformedEmailException();
        }
        // Email existence check
        UserAuth userAuth = jpaUserService.getUserAuthByEmail(email);
        if (null == userAuth) {
            return;
        }
        // Generate a password reset request token
        String token = simpleStatefulJwtService.generateToken(
                userAuth.getUserId().toString(),
                JwtAuthorisedAction.JWT_AUTHORISED_ACTION_PASSWORD_RESET,
                passwordResetRequestExpirationMillis);
        asyncJavaMailEmailService.sendPasswordResetEmail(token, email);
    }

    @Override
    public void passwordReset(String token, String password) throws ApiException {
        // Parameter validation
        try {
            String userId;
            synchronized (this) {
                userId = simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(token,
                        JwtAuthorisedAction.JWT_AUTHORISED_ACTION_PASSWORD_RESET);
                if (null == userId) {
                    throw new BadTokenException();
                }
                simpleStatefulJwtService.invalidateToken(token);
            }
            if (password.isBlank()) {
                throw new EmptyPasswordException();
            }
            String digest = bcryptPasswordEncoder.encode(password);
            jpaUserService.updateDigestByUserId(Integer.parseInt(userId), digest);
        } catch (UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException |
                 ExpiredJwtException |
                 IllegalArgumentException e) {
            throw new BadTokenException();
        }
    }

    @Override
    public UserDTO getUserDetailsByEmail(String email) {
        // get user details
        UserAuth userAuth = jpaUserService.getUserAuthByEmail(email);
        int userId = userAuth.getUserId();
        User user = jpaUserService.getUserById(userId);
        UserRole userRole = jpaUserService.getUserRolesByUserId(userId);

        // map data
        return new UserDTO(user.getDisplayName(), user.getAvatar(), userRole.getRole());
    }
}
