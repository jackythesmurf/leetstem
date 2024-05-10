package au.edu.sydney.elec5619.leetstem.service.db;

import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.entity.User;
import au.edu.sydney.elec5619.leetstem.entity.UserAuth;
import au.edu.sydney.elec5619.leetstem.entity.UserRole;
import au.edu.sydney.elec5619.leetstem.entity.UserSubject;

import java.util.List;

public interface UserService {
    void createUser(String displayName, String avatar, String email, String digest);

    UserAuth getUserAuthByEmail(String email);

    void updateDigestByUserId(int id, String digest);

    User getUserById(Integer id);

    String getUserEmailById(Integer id);

    void updateUserDisplayName(Integer userId, String displayName);

    void updateUserAvatar(Integer userId, String avatarId);

    List<UserSubject> getUserSubjectsByUserId(int id);

    List<UserBadgeDTO> getBadgesByUserId(Integer userId);

    void updateUserBadgeIsDisplayed(Integer userId, Integer badgeId, Boolean isDisplayed);

    void updateSelectedSubject(Integer userId, int subject, boolean is_selected);

    void updateEmailRequest(Integer userId, String token, String newEmail);

    void updateEmail(Integer userId);

    void removePendingEmailUpdate();

    UserRole getUserRolesByUserId(Integer userId);
}
