package au.edu.sydney.elec5619.leetstem.service.user.impl;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.constant.Role;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
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
import au.edu.sydney.elec5619.leetstem.service.user.UserDetailService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS;
import static au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction.JWT_AUTHORISED_ACTION_EMAIL_RESET;

@Service
public class UserDetailServiceFacade implements UserDetailService {
    private final StatefulJwtService simpleStatefulJwtService;
    private final UserService jpaUserService;
    private final PasswordEncoder bcryptPasswordEncoder;
    private final EmailService asyncJavaMailEmailService;
    private final ProfanityCheckService purgomalumProfanityCheckService;

    private final long emailResetRequestExpirationMillis;

    private final ConcurrentHashMap<Integer, Long> lastNCDetailsUpdateTimestamps = new ConcurrentHashMap<>();

    public UserDetailServiceFacade(StatefulJwtService simpleStatefulJwtService,
                                   UserService jpaUserService,
                                   PasswordEncoder bcryptPasswordEncoder,
                                   EmailService asyncJavaMailEmailService,
                                   ProfanityCheckService purgomalumProfanityCheckService,
                                   @Value("${leetstem.expiration.emailResetRequest}") long emailResetRequestExpirationMillis) {
        this.simpleStatefulJwtService = simpleStatefulJwtService;
        this.jpaUserService = jpaUserService;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.asyncJavaMailEmailService = asyncJavaMailEmailService;
        this.purgomalumProfanityCheckService = purgomalumProfanityCheckService;
        this.emailResetRequestExpirationMillis = emailResetRequestExpirationMillis;
    }


    @Override
    public Integer getUserIdByToken(String accessToken) throws ApiException {
        String userId;
        // null check
        if (accessToken == null) {
            throw new UnauthenticatedUserException();
        }
        // exception check
        try {
            userId =  simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(accessToken, JWT_AUTHORISED_ACTION_ACCESS);
        } catch (Exception e) {
            throw new UnauthenticatedUserException();
        }
        // unauthenticated user check
        if (userId == null) {
            throw new UnauthenticatedUserException();
        }
        return Integer.parseInt(userId);
    }

    @Override
    public String getUserDisplayNameById(Integer userId) {
        User user = jpaUserService.getUserById(userId);
        return user.getDisplayName();
    }

    @Override
    public String getUserAvatarById(Integer userId) {
        User user = jpaUserService.getUserById(userId);
        return user.getAvatar();
    }

    @Override
    public String getUserEmailById(Integer userId) {
        return jpaUserService.getUserEmailById(userId);
    }

    @Override
    public void updateNonCriticalDetails(Integer userId, String displayName, String avatarId) throws ApiException {
        // update frequency check
        Long lastUpdated = lastNCDetailsUpdateTimestamps.get(userId);
        if (lastUpdated != null) {
            long differenceInMillis = new Date().getTime() - lastUpdated;
            if (differenceInMillis < 60_000) {
                throw new TooMuchRequestsException();
            }
        }

        // profanity validation
        if (purgomalumProfanityCheckService.containProfanity(displayName)) {
            throw new BadContentException();
        }

        // update info
        if (displayName != null) {
            jpaUserService.updateUserDisplayName(userId, displayName);
        }
        if (avatarId != null) {
            jpaUserService.updateUserAvatar(userId, avatarId);
        }

        // Update the last update timestamp in memory
        lastNCDetailsUpdateTimestamps.put(userId, new Date().getTime());
    }

    @Override
    public List<UserBadgeDTO> getUserBadges(Integer userId) {
        // get user's badges
        return jpaUserService.getBadgesByUserId(userId);
    }

    @Override
    public boolean updateUserBadge(Integer userId, String badgeId, boolean isDisplayed) throws ApiException {
        // check null
        if (badgeId == null) {
            throw new BadBadgeIdException();
        }

        // check Badge Id existence
        List<UserBadgeDTO> userBadgeDTOList = jpaUserService.getBadgesByUserId(userId);
        UserBadgeDTO targetBadge = userBadgeDTOList.stream()
                .filter(userBadge -> badgeId.equals(userBadge.getBadgeId()))
                .findFirst()
                .orElseThrow(BadBadgeIdException::new);

        // check Displayed Badge amount
        if (isDisplayed && !targetBadge.isDisplayed()
                && userBadgeDTOList.stream().filter(UserBadgeDTO::isDisplayed).count() >= 3) {
            throw new TooMuchDisplayedBadgesException();
        }

        // update badge displayed
        try {
            jpaUserService.updateUserBadgeIsDisplayed(userId, Integer.parseInt(badgeId), isDisplayed);
        } catch (Exception e) {
            throw new BadBadgeIdException();
        }

        return isDisplayed;
    }

    @Override
    public boolean updateUserSubject(Integer userId, String subjectId, boolean isSelected) throws ApiException {
        // check null
        if (subjectId == null) {
            throw new BadSubjectIdException();
        }

        // check subject id existence
        int subject;
        try {
            subject = Integer.parseInt(subjectId);
            Subject.fromId(subject);
        } catch (NumberFormatException e) {
            throw new BadSubjectIdException();
        }

        // update selected subject
        jpaUserService.updateSelectedSubject(userId, subject, isSelected);

        return isSelected;
    }

    @Override
    public void updateUserPassword(Integer userId, String currentPassword, String newPassword) throws ApiException {
        // null, empty check
        if (currentPassword == null || newPassword == null
                || currentPassword.isEmpty() || newPassword.isEmpty()) {
            throw new EmptyPasswordException();
        }

        // current password check
        String email = jpaUserService.getUserEmailById(userId);
        UserAuth userAuth = jpaUserService.getUserAuthByEmail(email);
        if (null == userAuth) {
            throw new AuthenticationFailedException();
        }
        if (!bcryptPasswordEncoder.matches(currentPassword, userAuth.getDigest())) {
            throw new AuthenticationFailedException();
        }

        // password Strength check
        if (checkPasswordStrength(newPassword) == 0) {
            throw new EmptyPasswordException();
        }

        // generate password and update
        String digest = bcryptPasswordEncoder.encode(newPassword);
        jpaUserService.updateDigestByUserId(userId, digest);
    }

    @Override
    public void updateUserEmailRequest(Integer userId, String newEmail) throws ApiException {
        // parameter validation
        if (null == newEmail || !EmailValidator.getInstance().isValid(newEmail)) {
            throw new MalformedEmailException();
        }

        // email existence check
        String email = jpaUserService.getUserEmailById(userId);
        if (areEmailsEquivalent(email, newEmail)) {
            throw new MalformedEmailException();
        }

        // generate a email reset request token
        String token = simpleStatefulJwtService.generateToken(
                userId.toString(),
                JwtAuthorisedAction.JWT_AUTHORISED_ACTION_EMAIL_RESET,
                emailResetRequestExpirationMillis);
        asyncJavaMailEmailService.sendEmailResetEmail(token, newEmail);

        // update pending request
        jpaUserService.updateEmailRequest(userId, token, newEmail);
    }

    @Override
    public void updateUserEmail(Integer userId, String token) throws ApiException {
        // null check
        if (token == null || token.isEmpty()) {
            throw new BadTokenException();
        }

        // token's userId check
        try {
            String tokenUserId = simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(token, JWT_AUTHORISED_ACTION_EMAIL_RESET);
            if (tokenUserId == null || Integer.parseInt(tokenUserId) != userId) {
                throw new BadTokenException();
            }
        } catch (UnsupportedJwtException |
                 MalformedJwtException |
                 SignatureException |
                 ExpiredJwtException |
                 IllegalArgumentException e) {
            throw new BadTokenException();
        }

        // update and remove token
        simpleStatefulJwtService.invalidateToken(token);
        jpaUserService.updateEmail(userId);
    }

    @Override
    public void checkPrivilege(Integer userId) throws ApiException {
        UserRole userRole = jpaUserService.getUserRolesByUserId(userId);
        if (userRole == null) {
            throw new NonPrivilegedUserException();
        }

        // check roles
        boolean hasPrivilege;
        hasPrivilege = Objects.equals(userRole.getRole(), Role.TEACHER.getId());

        // check privilege
        if (!hasPrivilege) {
            throw new NonPrivilegedUserException();
        }
    }

    private boolean areEmailsEquivalent(String email1, String email2) {
        // case-insensitive
        email1 = email1.toLowerCase();
        email2 = email2.toLowerCase();

        String[] parts1 = email1.split("@");
        String[] parts2 = email2.split("@");

        // check domain
        if (!parts1[1].equals(parts2[1])) {
            return false;
        }

        // handle dot and plus addressing
        if (parts1[1].equals("gmail.com")) {
            parts1[0] = parts1[0].replace(".", "").split("\\+")[0];
            parts2[0] = parts2[0].replace(".", "").split("\\+")[0];
        }

        // final comparison
        return parts1[0].equals(parts2[0]);
    }

    private int checkPasswordStrength(String password) {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        return strength.getScore();
    }

}
