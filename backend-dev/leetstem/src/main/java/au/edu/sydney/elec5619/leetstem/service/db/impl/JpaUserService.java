package au.edu.sydney.elec5619.leetstem.service.db.impl;

import au.edu.sydney.elec5619.leetstem.constant.Role;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.entity.*;
import au.edu.sydney.elec5619.leetstem.repository.*;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class JpaUserService implements UserService {
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserSubjectRepository userSubjectRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserStatsRepository userStatsRepository;
    private final UserSubjectStatsRepository userSubjectStatsRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final PendingEmailUpdateRepository pendingEmailUpdateRepository;

    private final long emailUpdateRequestExpirationMillis;


    public JpaUserService(UserRepository userRepository,
                          UserAuthRepository userAuthRepository,
                          UserSubjectRepository userSubjectRepository,
                          UserRoleRepository userRoleRepository,
                          UserStatsRepository userStatsRepository,
                          UserSubjectStatsRepository userSubjectStatsRepository,
                          UserDetailRepository userDetailRepository,
                          UserBadgeRepository userBadgeRepository,
                          PendingEmailUpdateRepository pendingEmailUpdateRepository,
                          @Value("${leetstem.expiration.emailResetRequest}") long emailUpdateRequestExpirationMillis) {
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
        this.userSubjectRepository = userSubjectRepository;
        this.userRoleRepository = userRoleRepository;
        this.userStatsRepository = userStatsRepository;
        this.userSubjectStatsRepository = userSubjectStatsRepository;
        this.userDetailRepository = userDetailRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.pendingEmailUpdateRepository = pendingEmailUpdateRepository;
        this.emailUpdateRequestExpirationMillis = emailUpdateRequestExpirationMillis;
    }

    @Override
    public void createUser(String displayName, String avatar, String email, String digest) {
        // Create user
        User user = new User();
        user.setAvatar(avatar);
        user.setDisplayName(displayName);
        user = userRepository.save(user);

        // Create credentials
        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(user.getId());
        userAuth.setEmail(email);
        userAuth.setDigest(digest);
        userAuthRepository.save(userAuth);

        // Create user_subject entries
        List<UserSubject> userSubjects = new ArrayList<>();
        for (Subject subject : Subject.values()) {
            UserSubject userSubject = new UserSubject();
            userSubject.setSubject(subject.getId());
            userSubject.setSelected(false);
            userSubject.setUserId(user.getId());
            userSubjects.add(userSubject);
        }
        userSubjectRepository.saveAll(userSubjects);

        // Create user_role entry
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRole(Role.STUDENT.getId());
        userRoleRepository.save(userRole);

        // Create user_stats entry
        UserStats userStats = new UserStats();
        userStats.setUserId(user.getId());
        userStats.setTotalAttempted(0);
        userStats.setTotalCorrect(0);
        userStats.setTotalEndorsed(0);
        userStatsRepository.save(userStats);

        // Create user_subject_stats entries
        List<UserSubjectStats> userSubjectStatsList = new ArrayList<>();
        for (Subject subject : Subject.values()) {
            UserSubjectStats userSubjectStats = new UserSubjectStats();
            userSubjectStats.setUserId(user.getId());
            userSubjectStats.setSubject(subject.getId());
            userSubjectStats.setTotalAttempted(0);
            userSubjectStats.setTotalCorrect(0);
            userSubjectStats.setTotalCorrectFirst(0);
            userSubjectStatsList.add(userSubjectStats);
        }
        userSubjectStatsRepository.saveAll(userSubjectStatsList);

        // Create user_detail entry
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(user.getId());
        userDetailRepository.save(userDetail);
    }

    @Override
    public UserAuth getUserAuthByEmail(String email) {
        return userAuthRepository.findByEmail(email);
    }

    @Override
    public void updateDigestByUserId(int id, String digest) {
        userAuthRepository.updateDigestByUserId(id, digest);
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findUserById(id);
    }

    @Override
    public String getUserEmailById(Integer id) {
        return userRepository.findUserEmailById(id);
    }

    @Override
    public void updateUserDisplayName(Integer userId, String displayName) {
        User user = userRepository.findUserById(userId);
        if (displayName != null) {
            user.setDisplayName(displayName);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUserAvatar(Integer userId, String avatarId) {
        User user = userRepository.findUserById(userId);
        if (avatarId != null) {
            user.setAvatar(avatarId);
            userRepository.save(user);
        }
    }

    @Override
    public List<UserSubject> getUserSubjectsByUserId(int id) {
        return userSubjectRepository.findAllByUserId(id);
    }

    @Override
    public List<UserBadgeDTO> getBadgesByUserId(Integer userId) {
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);

        return userBadges.stream().map(userBadge -> {
            UserBadgeDTO userBadgeDTO = new UserBadgeDTO();
            userBadgeDTO.setBadgeId(userBadge.getBadgeId().toString());
            userBadgeDTO.setDisplayed(userBadge.getIsDisplayed());
            userBadgeDTO.setBadgeName(userBadge.getBadge().getDescription()); // replace name with description
            userBadgeDTO.setBadgeIcon(userBadge.getBadge().getIcon());
            return userBadgeDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateUserBadgeIsDisplayed(Integer userId, Integer badgeId, Boolean isDisplayed) {
        UserBadge userBadge = userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId)
                .orElseThrow(() -> new RuntimeException("UserBadge not found with badgeId: " + badgeId));

        userBadge.setIsDisplayed(isDisplayed);
        userBadgeRepository.save(userBadge);
    }

    @Override
    public void updateSelectedSubject(Integer userId, int subject, boolean is_selected) {
        Optional<UserSubject> optionalUserSubject =
                userSubjectRepository.findByUserIdAndSubject(userId, subject);

        // subject exists in user list
        if (optionalUserSubject.isPresent()) {
            UserSubject existingUserSubject = optionalUserSubject.get();
            existingUserSubject.setSelected(is_selected);
            userSubjectRepository.save(existingUserSubject);
            return;
        }

        // subject not in user list
        UserSubject newUserSubject = new UserSubject();
        newUserSubject.setUserId(userId);
        newUserSubject.setSubject(subject);
        newUserSubject.setSelected(is_selected);
        userSubjectRepository.save(newUserSubject);
    }

    @Override
    @Transactional
    public void updateEmailRequest(Integer userId, String token, String newEmail) {
        // Find existing record by user_id
        PendingEmailUpdate existingUpdate = pendingEmailUpdateRepository.findByUserId(userId).orElse(null);

        if (existingUpdate == null) {
            // Create new record
            existingUpdate = new PendingEmailUpdate();
            existingUpdate.setUserId(userId);
        }
        existingUpdate.setEmail(newEmail);

        // Save or Update
        pendingEmailUpdateRepository.save(existingUpdate);
    }

    @Override
    public void updateEmail(Integer userId) {
        // retrieve the entry with the specified userId
        PendingEmailUpdate pendingEmailUpdate = pendingEmailUpdateRepository
                .findByUserId(userId)
                .orElseThrow(null);

        if (pendingEmailUpdate == null) {
            return;
        }

        // extract email from retrieved entry
        String newEmail = pendingEmailUpdate.getEmail();

        // remove the entry from pending_email_reset
        pendingEmailUpdateRepository.delete(pendingEmailUpdate);

        // use userId and extracted email to update the user_auth
        UserAuth userAuth = userAuthRepository
                .findByUserId(userId)
                .orElseThrow(null);

        if (userAuth == null) {
            return;
        }

        userAuth.setEmail(newEmail);
        userAuthRepository.save(userAuth);
    }

    @Override
    public void removePendingEmailUpdate() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - emailUpdateRequestExpirationMillis);
        pendingEmailUpdateRepository.deletePendingEmailsByCreatedAtIsBefore(timestamp);
    }

    @Override
    public UserRole getUserRolesByUserId(Integer userId) {
        Optional<UserRole> userRoleOptional = userRoleRepository.findByUserId(userId);
        return userRoleOptional.orElse(null);
    }
}
