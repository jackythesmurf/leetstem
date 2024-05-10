package au.edu.sydney.elec5619.leetstem.service.user.impl;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.constant.Role;
import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.entity.User;
import au.edu.sydney.elec5619.leetstem.entity.UserAuth;
import au.edu.sydney.elec5619.leetstem.entity.UserRole;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.*;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import au.edu.sydney.elec5619.leetstem.service.email.EmailService;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import au.edu.sydney.elec5619.leetstem.service.profanity.ProfanityCheckService;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserDetailServiceFacadeTest {
    private UserDetailServiceFacade userDetailServiceFacade;

    private StatefulJwtService simpleStatefulJwtServiceMocked;

    private UserService jpaUserServiceMocked;

    private PasswordEncoder bcryptPasswordEncoderMocked;

    private EmailService asyncJavaMailEmailServiceMocked;

    private ProfanityCheckService purgomalumProfanityCheckServiceMocked;

    private long emailResetRequestExpirationMillisMocked;

    @BeforeEach
    public void setUp() {
        // mocks
        simpleStatefulJwtServiceMocked = Mockito.mock(StatefulJwtService.class);
        jpaUserServiceMocked = Mockito.mock(UserService.class);
        bcryptPasswordEncoderMocked = Mockito.mock(PasswordEncoder.class);
        asyncJavaMailEmailServiceMocked = Mockito.mock(EmailService.class);
        purgomalumProfanityCheckServiceMocked = Mockito.mock(ProfanityCheckService.class);
        emailResetRequestExpirationMillisMocked = 600_000;

        // setup
        userDetailServiceFacade = new UserDetailServiceFacade(
                simpleStatefulJwtServiceMocked,
                jpaUserServiceMocked,
                bcryptPasswordEncoderMocked,
                asyncJavaMailEmailServiceMocked,
                purgomalumProfanityCheckServiceMocked,
                emailResetRequestExpirationMillisMocked);

    }

    @Nested
    class GetUserIdByTokenTest {
        @Test
        public void exceptionTest() throws ApiException {
            // null input
            assertThrows(UnauthenticatedUserException.class, () -> userDetailServiceFacade.getUserIdByToken(null));

            // invalid token
            String invalidToken = "invalid token";
            when(simpleStatefulJwtServiceMocked.getSubjectIdIfAuthorisedTo(
                    eq(invalidToken),
                    any(JwtAuthorisedAction.class)
            )).thenThrow(new UnsupportedJwtException("invalid token"));
            assertThrows(UnauthenticatedUserException.class, () -> userDetailServiceFacade.getUserIdByToken(invalidToken));

            // null token
            String nullToken = "null token";
            when(simpleStatefulJwtServiceMocked.getSubjectIdIfAuthorisedTo(
                    eq(nullToken),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS)
            )).thenReturn(null);
            assertThrows(UnauthenticatedUserException.class, () -> userDetailServiceFacade.getUserIdByToken(nullToken));
        }

        @Test
        public void normalReturnTest() throws ApiException {
            // valid token
            String validToken = "valid token";
            when(simpleStatefulJwtServiceMocked.getSubjectIdIfAuthorisedTo(
                    eq(validToken),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS)
            )).thenReturn(String.valueOf(1));
            assertEquals(1, userDetailServiceFacade.getUserIdByToken(validToken));
        }
    }

    @Nested
    class GetUserDetailsTest {
        @Test
        public void getUserDisplayNameTest() {
            // normal user
            User user = new User();
            user.setId(1);
            String username = "test user";
            user.setDisplayName(username);
            when(jpaUserServiceMocked.getUserById(eq(1))).thenReturn(user);
            assertEquals(username, userDetailServiceFacade.getUserDisplayNameById(1));
        }

        @Test
        public void getUserAvatarTest() {
            // normal user
            User user = new User();
            user.setId(1);
            String userAvatar = "test/avatar.png";
            user.setAvatar(userAvatar);
            when(jpaUserServiceMocked.getUserById(eq(1))).thenReturn(user);
            assertEquals(userAvatar, userDetailServiceFacade.getUserAvatarById(1));
        }

        @Test
        public void getUserEmailTest() {
            // normal user
            String userEmail = "user@test.com";
            when(jpaUserServiceMocked.getUserEmailById(eq(1))).thenReturn(userEmail);
            assertEquals(userEmail, userDetailServiceFacade.getUserEmailById(1));
        }
    }

    @Nested
    class UpdateNCDetailsTest {
        @Test
        public void exceptionTest() throws ApiException {
            // setup
            String badWords = "bad words";
            when(purgomalumProfanityCheckServiceMocked.containProfanity(eq(badWords))).thenReturn(true);

            // bad content exception
            assertThrows(BadContentException.class,
                    () -> userDetailServiceFacade.updateNonCriticalDetails(1, badWords, "test"));

            // update frequency exception
            userDetailServiceFacade.updateNonCriticalDetails(1, "test1", "test1");
            assertThrows(TooMuchRequestsException.class,
                    () -> userDetailServiceFacade.updateNonCriticalDetails(1, "test1", "test1"));
        }

        @Test
        public void normalTest() throws ApiException, InterruptedException {
            // setup
            String goodWords = "good words";
            when(purgomalumProfanityCheckServiceMocked.containProfanity(eq(goodWords))).thenReturn(false);

            // 1st request
            userDetailServiceFacade.updateNonCriticalDetails(1, goodWords, "test1");
            verify(jpaUserServiceMocked, times(1)).updateUserDisplayName(1, goodWords);
            verify(jpaUserServiceMocked, times(1)).updateUserAvatar(1, "test1");

            // 2nd request
            Thread.sleep(60_000);
            userDetailServiceFacade.updateNonCriticalDetails(1, null, null);
            verify(jpaUserServiceMocked, times(0)).updateUserDisplayName(1, null);
            verify(jpaUserServiceMocked, times(0)).updateUserAvatar(1, null);
        }
    }

    @Nested
    class UserBadgeManagementTest {
        @Test
        public void getUserBadgesTest() {
            List<UserBadgeDTO> userBadgeDTOList = new ArrayList<>();
            when(jpaUserServiceMocked.getBadgesByUserId(eq(1))).thenReturn(userBadgeDTOList);

            assertEquals(userBadgeDTOList, userDetailServiceFacade.getUserBadges(1));
            // verify time
            verify(jpaUserServiceMocked, times(1)).getBadgesByUserId(1);
        }

        @Test
        public void exceptionTest() throws ApiException {
            // null input
            assertThrows(BadBadgeIdException.class,
                    () -> userDetailServiceFacade.updateUserBadge(1, null, true));

            // No Badge Id existence
            List<UserBadgeDTO> emptyUserBadgeDTOList = new ArrayList<>();
            when(jpaUserServiceMocked.getBadgesByUserId(eq(1))).thenReturn(emptyUserBadgeDTOList);
            assertThrows(BadBadgeIdException.class,
                    () -> userDetailServiceFacade.updateUserBadge(1, "1", true));

            // Too much displayed badges
            List<UserBadgeDTO> userBadgeDTOList = new ArrayList<>();

            UserBadgeDTO userBadgeDTOOne = new UserBadgeDTO();
            userBadgeDTOOne.setBadgeId(String.valueOf(1));
            userBadgeDTOOne.setDisplayed(true);
            userBadgeDTOList.add(userBadgeDTOOne);
            UserBadgeDTO userBadgeDTOTwo = new UserBadgeDTO();
            userBadgeDTOTwo.setBadgeId(String.valueOf(2));
            userBadgeDTOTwo.setDisplayed(true);
            userBadgeDTOList.add(userBadgeDTOTwo);
            UserBadgeDTO userBadgeDTOThree = new UserBadgeDTO();
            userBadgeDTOThree.setBadgeId(String.valueOf(3));
            userBadgeDTOThree.setDisplayed(true);
            userBadgeDTOList.add(userBadgeDTOThree);
            UserBadgeDTO userBadgeDTOFour = new UserBadgeDTO();
            userBadgeDTOFour.setBadgeId(String.valueOf(4));
            userBadgeDTOFour.setDisplayed(false);
            userBadgeDTOList.add(userBadgeDTOFour);

            when(jpaUserServiceMocked.getBadgesByUserId(eq(2))).thenReturn(userBadgeDTOList);
            assertThrows(TooMuchDisplayedBadgesException.class,
                    () -> userDetailServiceFacade.updateUserBadge(2, "4", true));

            // Badge update failed
            doThrow(new RuntimeException()).
                    when(jpaUserServiceMocked).updateUserBadgeIsDisplayed(eq(2), eq(3), anyBoolean());
            assertThrows(BadBadgeIdException.class,
                    () -> userDetailServiceFacade.updateUserBadge(2, "3", true));
        }

        @Test
        public void normalTest() throws ApiException {
            List<UserBadgeDTO> userBadgeDTOList = new ArrayList<>();
            UserBadgeDTO userBadgeDTOOne = new UserBadgeDTO();
            userBadgeDTOOne.setBadgeId(String.valueOf(1));
            userBadgeDTOOne.setDisplayed(false);
            userBadgeDTOList.add(userBadgeDTOOne);
            UserBadgeDTO userBadgeDTOTwo = new UserBadgeDTO();
            userBadgeDTOTwo.setBadgeId(String.valueOf(2));
            userBadgeDTOTwo.setDisplayed(true);
            userBadgeDTOList.add(userBadgeDTOTwo);

            when(jpaUserServiceMocked.getBadgesByUserId(eq(2))).thenReturn(userBadgeDTOList);

            assertTrue(userDetailServiceFacade.updateUserBadge(2, "1", true));
            assertFalse(userDetailServiceFacade.updateUserBadge(2, "2", false));

            UserBadgeDTO userBadgeDTOThree = new UserBadgeDTO();
            userBadgeDTOThree.setBadgeId(String.valueOf(3));
            userBadgeDTOThree.setDisplayed(true);
            userBadgeDTOList.add(userBadgeDTOThree);
            UserBadgeDTO userBadgeDTOFour = new UserBadgeDTO();
            userBadgeDTOFour.setBadgeId(String.valueOf(4));
            userBadgeDTOFour.setDisplayed(true);
            userBadgeDTOList.add(userBadgeDTOFour);

            when(jpaUserServiceMocked.getBadgesByUserId(eq(2))).thenReturn(userBadgeDTOList);

            assertFalse(userDetailServiceFacade.updateUserBadge(2, "2", false));
            assertFalse(userDetailServiceFacade.updateUserBadge(2, "3", false));
            assertTrue(userDetailServiceFacade.updateUserBadge(2, "4", true));
        }
    }

    @Nested
    class UpdateUserSubjectTest {
        @Test
        public void exceptionTest() {
            // null check
            assertThrows(BadSubjectIdException.class,
                    () -> userDetailServiceFacade.updateUserSubject(1, null, false));

            // invalid subject format
            assertThrows(BadSubjectIdException.class,
                    () -> userDetailServiceFacade.updateUserSubject(1, "not integer", true));

            // no subject existence
            assertThrows(BadSubjectIdException.class,
                    () -> userDetailServiceFacade.updateUserSubject(1, "-1", true));
        }

        @Test
        public void normalTest() throws ApiException {
            userDetailServiceFacade.updateUserSubject(1, "15030", false);
            verify(jpaUserServiceMocked, times(1)).updateSelectedSubject(1, 15030, false);

            userDetailServiceFacade.updateUserSubject(2, "15050", true);
            verify(jpaUserServiceMocked, times(1)).updateSelectedSubject(2, 15050, true);

        }
    }

    @Nested
    class UpdateUserPasswordTest {
        @Test
        public void exceptionTest() {
            // null, empty
            assertThrows(EmptyPasswordException.class,
                    () -> userDetailServiceFacade.updateUserPassword(1, null, null));
            assertThrows(EmptyPasswordException.class,
                    () -> userDetailServiceFacade.updateUserPassword(1, "", ""));
            assertThrows(EmptyPasswordException.class,
                    () -> userDetailServiceFacade.updateUserPassword(1, "123", null));
            assertThrows(EmptyPasswordException.class,
                    () -> userDetailServiceFacade.updateUserPassword(1, null, "123"));
            assertThrows(EmptyPasswordException.class,
                    () -> userDetailServiceFacade.updateUserPassword(1, "123", ""));
            assertThrows(EmptyPasswordException.class,
                    () -> userDetailServiceFacade.updateUserPassword(1, "", "123"));

            // no user existence
            when(jpaUserServiceMocked.getUserEmailById(eq(1))).thenReturn("no user");
            when(jpaUserServiceMocked.getUserAuthByEmail(eq("no user"))).thenReturn(null);
            assertThrows(AuthenticationFailedException.class,
                    () -> userDetailServiceFacade.updateUserPassword(1, "test", "test"));

            // password match failed
            UserAuth userAuth = new UserAuth();
            userAuth.setDigest("password");
            when(jpaUserServiceMocked.getUserEmailById(eq(2))).thenReturn("user@test.com");
            when(jpaUserServiceMocked.getUserAuthByEmail(eq("user@test.com"))).thenReturn(userAuth);
            when(bcryptPasswordEncoderMocked.matches(anyString(), anyString())).thenReturn(false);
            assertThrows(AuthenticationFailedException.class,
                    () -> userDetailServiceFacade.updateUserPassword(2, "test", "test"));

            // weak password
            when(bcryptPasswordEncoderMocked.matches(eq("password"), eq("password"))).thenReturn(true);
            assertThrows(EmptyPasswordException.class,
                    () -> userDetailServiceFacade.updateUserPassword(2, "password", "123456"));

        }

        @Test
        public void normalTest() throws ApiException {
            UserAuth userAuth = new UserAuth();
            userAuth.setDigest("password");
            when(jpaUserServiceMocked.getUserEmailById(eq(2))).thenReturn("user@test.com");
            when(jpaUserServiceMocked.getUserAuthByEmail(eq("user@test.com"))).thenReturn(userAuth);
            when(bcryptPasswordEncoderMocked.matches(anyString(), anyString())).thenReturn(true);
            when(bcryptPasswordEncoderMocked.encode(anyString())).thenReturn("1234567!!");

            userDetailServiceFacade.updateUserPassword(2, "123", "1234567!!");

            verify(bcryptPasswordEncoderMocked, times(1)).encode("1234567!!");
            verify(jpaUserServiceMocked, times(1)).updateDigestByUserId(2, "1234567!!");
        }
    }

    @Nested
    class updateUserEmailRequestTest {
        @Test
        public void exceptionTest() {
            // email format check
            assertThrows(MalformedEmailException.class,
                    () -> userDetailServiceFacade.updateUserEmailRequest(1, null));
            assertThrows(MalformedEmailException.class,
                    () -> userDetailServiceFacade.updateUserEmailRequest(1, "test"));

            // email existence
            when(jpaUserServiceMocked.getUserEmailById(anyInt())).thenReturn("johndoe@gmail.com");
            assertThrows(MalformedEmailException.class,
                    () -> userDetailServiceFacade.updateUserEmailRequest(1, "john.doe@gmail.com"));
            assertThrows(MalformedEmailException.class,
                    () -> userDetailServiceFacade.updateUserEmailRequest(1, "john.doe+123@gmail.com"));
        }

        @Test
        public void normalTest() throws ApiException {
            when(jpaUserServiceMocked.getUserEmailById(anyInt())).thenReturn("johndoe@gmail.com");
            when(simpleStatefulJwtServiceMocked.generateToken(
                    eq("2"),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_EMAIL_RESET),
                    eq(emailResetRequestExpirationMillisMocked))
            ).thenReturn("token");

            userDetailServiceFacade.updateUserEmailRequest(2, "test@gmail.com");
            verify(asyncJavaMailEmailServiceMocked, times(1)).
                    sendEmailResetEmail("token", "test@gmail.com");
            verify(jpaUserServiceMocked, times(1)).
                    updateEmailRequest(2, "token", "test@gmail.com");

            when(jpaUserServiceMocked.getUserEmailById(anyInt())).thenReturn("johndoe@outlook.com");
            userDetailServiceFacade.updateUserEmailRequest(2, "test@outlook.com");
            verify(asyncJavaMailEmailServiceMocked, times(1)).
                    sendEmailResetEmail("token", "test@outlook.com");
            verify(jpaUserServiceMocked, times(1)).
                    updateEmailRequest(2, "token", "test@outlook.com");

            when(jpaUserServiceMocked.getUserEmailById(anyInt())).thenReturn("johndoe@gmail.com");
            userDetailServiceFacade.updateUserEmailRequest(2, "test@test.com");
            verify(asyncJavaMailEmailServiceMocked, times(1)).
                    sendEmailResetEmail("token", "test@test.com");
            verify(jpaUserServiceMocked, times(1)).
                    updateEmailRequest(2, "token", "test@test.com");
        }
    }

    @Nested
    class UpdateUserEmailTest {
        @Test
        public void exceptionTest() {
            // null input
            assertThrows(BadTokenException.class,
                    () -> userDetailServiceFacade.updateUserEmail(1, null));
            assertThrows(BadTokenException.class,
                    () -> userDetailServiceFacade.updateUserEmail(1, ""));

            // invalid token
            String invalidToken = "invalid token";
            when(simpleStatefulJwtServiceMocked.getSubjectIdIfAuthorisedTo(
                    eq(invalidToken),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_EMAIL_RESET)
            )).thenThrow(new UnsupportedJwtException("invalid token"));
            assertThrows(BadTokenException.class,
                    () -> userDetailServiceFacade.updateUserEmail(1, invalidToken));

            // null token
            String nullToken = "null token";
            when(simpleStatefulJwtServiceMocked.getSubjectIdIfAuthorisedTo(
                    eq(nullToken),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_EMAIL_RESET)
            )).thenReturn(null);
            assertThrows(BadTokenException.class,
                    () -> userDetailServiceFacade.updateUserEmail(1, nullToken));

            // user match failed
            String misMatchedToken = "mismatched token";
            when(simpleStatefulJwtServiceMocked.getSubjectIdIfAuthorisedTo(
                    eq(misMatchedToken),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_EMAIL_RESET)
            )).thenReturn("2");
            assertThrows(BadTokenException.class,
                    () -> userDetailServiceFacade.updateUserEmail(1, misMatchedToken));
        }

        @Test
        public void normalTest() throws ApiException {
            String validToken = "valid token";
            when(simpleStatefulJwtServiceMocked.getSubjectIdIfAuthorisedTo(
                    eq(validToken),
                    eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_EMAIL_RESET)
            )).thenReturn("1");

            userDetailServiceFacade.updateUserEmail(1, validToken);

            verify(simpleStatefulJwtServiceMocked, times(1)).invalidateToken(validToken);
            verify(jpaUserServiceMocked, times(1)).updateEmail(1);
        }

    }

    @Nested
    class CheckPrivilegeTest {
        @Test
        public void exceptionTest() {
            // null role list
            when(jpaUserServiceMocked.getUserRolesByUserId(anyInt())).thenReturn(null);
            assertThrows(NonPrivilegedUserException.class,
                    () -> userDetailServiceFacade.checkPrivilege(1));

            // no privilege role
            UserRole userRole = new UserRole();
            userRole.setRole(Role.STUDENT.getId());
            when(jpaUserServiceMocked.getUserRolesByUserId(eq(1))).thenReturn(userRole);
            assertThrows(NonPrivilegedUserException.class,
                    () -> userDetailServiceFacade.checkPrivilege(1));
        }

        @Test
        public void normalTest() throws ApiException {
            UserRole userRole = new UserRole();
            userRole.setRole(Role.TEACHER.getId());
            when(jpaUserServiceMocked.getUserRolesByUserId(eq(1))).thenReturn(userRole);
            userDetailServiceFacade.checkPrivilege(1);
        }
    }
}
