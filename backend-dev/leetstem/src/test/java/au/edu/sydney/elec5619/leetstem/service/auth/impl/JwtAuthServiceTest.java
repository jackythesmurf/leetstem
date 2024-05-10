package au.edu.sydney.elec5619.leetstem.service.auth.impl;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.entity.PendingUser;
import au.edu.sydney.elec5619.leetstem.entity.UserAuth;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.AuthenticationFailedException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadTokenException;
import au.edu.sydney.elec5619.leetstem.exception.impl.EmptyPasswordException;
import au.edu.sydney.elec5619.leetstem.exception.impl.MalformedEmailException;
import au.edu.sydney.elec5619.leetstem.service.db.PendingUserService;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import au.edu.sydney.elec5619.leetstem.service.email.EmailService;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JwtAuthServiceTest {
    private StatefulJwtService mockedStatefulJwtService;
    private UserService mockedUserService;
    private EmailService mockedEmailService;
    private PasswordEncoder mockedPasswordEncoder;
    private PendingUserService mockedPendingUserService;
    private long accessExpirationMillis;
    private long signupRequestExpirationMillis;
    private long passwordResetRequestExpirationMillis;
    private String email;
    private String password;
    private String token;
    private JwtAuthService jwtAuthService;
    private Random random;

    @BeforeEach
    void setup() {
        mockedStatefulJwtService = Mockito.mock(StatefulJwtService.class);
        mockedUserService = Mockito.mock(UserService.class);
        mockedEmailService = Mockito.mock(EmailService.class);
        mockedPasswordEncoder = Mockito.mock(PasswordEncoder.class);
        mockedPendingUserService = Mockito.mock(PendingUserService.class);

        random = new Random(5619);
        accessExpirationMillis = random.nextLong();
        signupRequestExpirationMillis = random.nextLong();
        passwordResetRequestExpirationMillis = random.nextLong();

        password = String.valueOf(random.nextInt());
        email = random.nextInt() + "@leetstem.com";
        token = String.valueOf(random.nextInt());

        jwtAuthService = new JwtAuthService(mockedStatefulJwtService,
                mockedUserService,
                mockedEmailService,
                mockedPasswordEncoder,
                mockedPendingUserService,
                accessExpirationMillis,
                signupRequestExpirationMillis,
                passwordResetRequestExpirationMillis);
    }

    @Nested
    class SignInTest {
        @Test
        void shouldThrowMalformedEmailExceptionOnInvalidEmailAddresses() {
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.signIn(null, password));
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.signIn("", password));
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.signIn("bad.email.addr", password));
        }

        @Test
        void shouldThrowEmptyPasswordExceptionOnNullOrEmptyPasswords() {
            assertThrows(EmptyPasswordException.class, () -> jwtAuthService.signIn(email, null));
            assertThrows(EmptyPasswordException.class, () -> jwtAuthService.signIn(email, ""));
        }

        @Test
        void shouldThrowAuthenticationFailedExceptionOnNonExistentAccount() {
            // Given the prepared email doesn't exist in db
            when(mockedUserService.getUserAuthByEmail(eq(email))).thenReturn(null);
            // When authenticating with the email,
            // Then AuthenticationFailedException should be thrown
            assertThrows(AuthenticationFailedException.class, () -> jwtAuthService.signIn(email, password));
        }

        @Test
        void shouldThrowAuthenticationFailedExceptionOnUnmatchedCredentials() {
            // Given password encoder not matching the prepared password
            when(mockedPasswordEncoder.matches(eq(password), any())).thenReturn(false);
            // Given the prepared email exists in db
            UserAuth userAuth = new UserAuth();
            when(mockedUserService.getUserAuthByEmail(eq(email))).thenReturn(userAuth);
            // When authenticating with the prepared email and password,
            // Then AuthenticationFailedException should be thrown
            assertThrows(AuthenticationFailedException.class, () -> jwtAuthService.signIn(email, password));
        }

        @Test
        void shouldReturnGeneratedTokenOnValidCredentials() throws ApiException {
            // Given password encoder not matching the prepared password
            when(mockedPasswordEncoder.matches(eq(password), any())).thenReturn(true);
            // Given the prepared email does exist in db
            Integer userId = random.nextInt();
            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(userId);
            when(mockedUserService.getUserAuthByEmail(eq(email))).thenReturn(userAuth);
            // Given the JWT service returns a prepared token
            when(mockedStatefulJwtService.generateToken(
                    eq(userId.toString()),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS),
                    eq(accessExpirationMillis)
            )).thenReturn(token);
            // When authenticating with the prepared email and password
            String token = jwtAuthService.signIn(email, password);
            // Then the token generated by JWT service to authorise access should be returned
            assertEquals(JwtAuthServiceTest.this.token, token);
        }
    }

    @Nested
    class SignOutTest {
        @Test
        void shouldInvalidateToken() {
            // When calling signout
            jwtAuthService.signOut(token);
            // Then JWT service should have been called to invalidate the token
            verify(mockedStatefulJwtService, times(1))
                    .invalidateToken(eq(token));
        }
    }

    @Nested
    class SignUpRequestTest {
        @Test
        void shouldThrowMalformedEmailExceptionOnInvalidEmailAddresses() {
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.signUpRequest(null, password));
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.signUpRequest("", password));
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.signUpRequest("bad.email.addr", password));
        }

        @Test
        void shouldThrowEmptyPasswordExceptionOnNullOrEmptyPasswords() {
            assertThrows(EmptyPasswordException.class, () -> jwtAuthService.signUpRequest(email, null));
            assertThrows(EmptyPasswordException.class, () -> jwtAuthService.signUpRequest(email, ""));
        }

        @Test
        void shouldNotTriggerEmailSendingOnExistingEmails() throws ApiException {
            // Given the prepared email exists in db
            UserAuth userAuth = new UserAuth();
            when(mockedUserService.getUserAuthByEmail(eq(email))).thenReturn(userAuth);
            // When calling signUpRequest
            jwtAuthService.signUpRequest(email, password);
            // Then the email service shouldn't have been triggered
            verify(mockedEmailService, never()).sendSignUpVerificationEmail(any(), any());
        }

        @Test
        void shouldSavePendingUserAndTriggerEmailWithGeneratedToken() throws ApiException {
            // Given the password can be hashed to a digest
            String digest = String.valueOf(random.nextInt());
            when(mockedPasswordEncoder.encode(eq(password))).thenReturn(digest);
            // Given a pending user to be created by user service
            PendingUser pendingUser = new PendingUser();
            Integer pendingUserId = random.nextInt();
            pendingUser.setId(pendingUserId);
            when(mockedPendingUserService.insertPendingUser(
                    eq(email),
                    eq(digest)
            )).thenReturn(pendingUser);
            // Given the JWT service generates token based on the pending user's id
            when(mockedStatefulJwtService.generateToken(
                    eq(pendingUserId.toString()),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_SIGN_UP),
                    eq(signupRequestExpirationMillis)
            )).thenReturn(token);
            // When calling signUpRequest with a new email and password
            jwtAuthService.signUpRequest(email, password);
            // Then the email and digest should have been saved
            verify(mockedPendingUserService, times(1))
                    .insertPendingUser(eq(email), eq(digest));
            // And the email service's verification method should have been called
            verify(mockedEmailService, times(1))
                    .sendSignUpVerificationEmail(eq(token), eq(email));
        }
    }

    @Nested
    class SignUpVerifyTest {

        static Stream<Class<? extends Throwable>> provideExceptionsOnBadToken() {
            return Stream.of(
                    UnsupportedJwtException.class,
                    MalformedJwtException.class,
                    ExpiredJwtException.class,
                    IllegalArgumentException.class
            );
        }

        @ParameterizedTest
        @MethodSource("provideExceptionsOnBadToken")
        void shouldThrowBadTokenExceptionOnAnyExceptionThrownByJwtService(Class<? extends Throwable> exceptionClass) {
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(any(), any()))
                    .thenThrow(exceptionClass);
            assertThrows(BadTokenException.class, () -> jwtAuthService.signUpVerify(token));
        }

        @Test
        void shouldThrowBadTokenExceptionOnNullUserId() {
            // Given a digest, but no user ID set
            String digest = String.valueOf(random.nextInt());
            when(mockedPasswordEncoder.encode(password)).thenReturn(digest);
            // When reset a password with a valid token
            // Then BadTokenException should be thrown
            assertThrows(BadTokenException.class, () -> jwtAuthService.signUpVerify(token));
        }

        @Test
        void shouldCreateUserAndRemovePendingUser() throws ApiException {
            // Given a pending user to be returned when querying with a certain ID
            Integer pendingUserId = random.nextInt();
            PendingUser pendingUser = new PendingUser();
            pendingUser.setId(pendingUserId);
            pendingUser.setDigest(password);
            pendingUser.setEmail(email);
            when(mockedPendingUserService.getPendingUserById(eq(pendingUserId)))
                    .thenReturn(pendingUser);
            // Given the JWT service returns the above ID on parsing tokens
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    eq(token),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_SIGN_UP)
            )).thenReturn(pendingUserId.toString());
            // When calling signUpVerify
            jwtAuthService.signUpVerify(token);
            // Then user service should have been called to create a user
            verify(mockedUserService, times(1))
                    .createUser(any(), any(), eq(email), eq(password));
            // And pending user service should have been invoked to remove the pending user record
            verify(mockedPendingUserService, times(1))
                    .removePendingUserById(eq(pendingUserId));
        }
    }

    @Nested
    class PasswordResetRequestTest {
        @Test
        void shouldThrowMalformedEmailExceptionOnInvalidEmailAddresses() {
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.passwordResetRequest(null));
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.passwordResetRequest(""));
            assertThrows(MalformedEmailException.class, () -> jwtAuthService.passwordResetRequest("bad.email.addr"));
        }

        @Test
        void shouldNotTriggerEmailSendingOnNonExistentEmails() throws ApiException {
            // Given the prepared email does not exist in db
            // When calling passwordResetRequest
            jwtAuthService.passwordResetRequest(email);
            // Then the email service shouldn't have been triggered
            verify(mockedEmailService, never()).sendSignUpVerificationEmail(any(), any());
        }

        @Test
        void shouldTriggerEmailWithGeneratedToken() throws ApiException {
            // Given the prepared email does exist in db
            Integer userId = random.nextInt();
            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(userId);
            when(mockedUserService.getUserAuthByEmail(eq(email))).thenReturn(userAuth);
            // Given the JWT service returns a prepared token
            when(mockedStatefulJwtService.generateToken(
                    eq(userId.toString()),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_PASSWORD_RESET),
                    eq(passwordResetRequestExpirationMillis)
            )).thenReturn(token);
            // When calling passwordResetRequest with the email
            jwtAuthService.passwordResetRequest(email);
            // Then a token should have been sent in a password reset email
            verify(mockedEmailService, times(1))
                    .sendPasswordResetEmail(eq(token), eq(email));
        }
    }

    @Nested
    class PasswordResetTest {
        static Stream<Class<? extends Throwable>> provideExceptionsOnBadToken() {
            return Stream.of(
                    UnsupportedJwtException.class,
                    MalformedJwtException.class,
                    ExpiredJwtException.class,
                    IllegalArgumentException.class
            );
        }

        @ParameterizedTest
        @MethodSource("provideExceptionsOnBadToken")
        void shouldThrowBadTokenExceptionOnAnyExceptionThrownByJwtService(Class<? extends Throwable> exceptionClass) {
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(any(), any()))
                    .thenThrow(exceptionClass);
            assertThrows(BadTokenException.class, () -> jwtAuthService.passwordReset(token, password));
        }

        @Test
        void shouldThrowBadTokenExceptionOnNullUserId() {
            // Given a digest, but no user ID set
            String digest = String.valueOf(random.nextInt());
            when(mockedPasswordEncoder.encode(password)).thenReturn(digest);
            // When reset a password with a valid token
            // Then BadTokenException should be thrown
            assertThrows(BadTokenException.class, () -> jwtAuthService.passwordReset(token, password));
        }

        @Test
        void shouldUpdateThroughUserService() throws ApiException {
            // Given a user ID
            String userId = String.valueOf(random.nextInt());
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    eq(token),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_PASSWORD_RESET)
            )).thenReturn(userId);
            // Given a digest
            String digest = String.valueOf(random.nextInt());
            when(mockedPasswordEncoder.encode(password)).thenReturn(digest);
            // When reset a password with a valid token
            jwtAuthService.passwordReset(token, password);
            // Then user service should have been called to update database
            verify(mockedUserService, times(1))
                    .updateDigestByUserId(eq(Integer.parseInt(userId)), eq(digest));
        }
    }
}
