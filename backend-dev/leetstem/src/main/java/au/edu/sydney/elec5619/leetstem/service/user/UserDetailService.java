package au.edu.sydney.elec5619.leetstem.service.user;

import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;

import java.util.List;

public interface UserDetailService {

    Integer getUserIdByToken(String accessToken) throws ApiException;

    String getUserDisplayNameById(Integer userId);

    String getUserAvatarById(Integer userId);

    String getUserEmailById(Integer userId);

    void updateNonCriticalDetails(Integer userId, String displayName, String avatarId) throws ApiException;

    List<UserBadgeDTO> getUserBadges(Integer userId);

    boolean updateUserBadge(Integer userId, String badgeId, boolean isDisplayed) throws ApiException;

    boolean updateUserSubject(Integer userId, String subjectId, boolean isSelected) throws ApiException;

    void updateUserPassword(Integer userId, String currentPassword, String newPassword) throws ApiException;

    void updateUserEmailRequest(Integer userId, String newEmail) throws ApiException;

    void updateUserEmail(Integer userId, String token) throws ApiException;

    void checkPrivilege(Integer userId) throws ApiException;
}
