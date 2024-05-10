package au.edu.sydney.elec5619.leetstem.controller;


import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.payload.request.user.*;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.SimpleResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.user.UserBadgeUpdateResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.user.UserBadgesResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.user.UserDetailResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.user.UserSubjectUpdateResponse;
import au.edu.sydney.elec5619.leetstem.service.user.UserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/user")
@RestController
@Tag(name = "User details implementation", description = "Endpoints of user details and actions")
public class UserController {

    private final UserDetailService userDetailServiceImpl;

    public UserController(UserDetailService userDetailServiceImpl) {
        this.userDetailServiceImpl = userDetailServiceImpl;
    }

    @Operation(summary = "5.1 Details of the signed-in user",
            description = "")
    @GetMapping("/details")
    public UserDetailResponse getUserDetails(@CookieValue(value = "accessToken", required = false) String accessToken)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get user details
        String userEmail = userDetailServiceImpl.getUserEmailById(userId);
        String userDisplayName = userDetailServiceImpl.getUserDisplayNameById(userId);
        String userAvatar = userDetailServiceImpl.getUserAvatarById(userId);

        // user displayed badges
        List<UserBadgeDTO> badges = userDetailServiceImpl.getUserBadges(userId);
        List<UserBadgeDTO> filteredBadges = badges.stream()
                .filter(badge -> badge.isDisplayed())
                .collect(Collectors.toList());

        // response
        return new UserDetailResponse(ErrorCode.ERROR_CODE_OK, userDisplayName, userAvatar, userEmail, filteredBadges);
    }

    @Operation(summary = "5.2 Update non-critical details of the signed-in user",
            description = "")
    @PostMapping("/details/update/nc")
    public SimpleResponse updateNonCriticalDetails(@CookieValue(value = "accessToken", required = false) String accessToken,
                                                   @Valid @RequestBody NCUserDetailUpdateRequest ncUserDetailUpdateRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get request info
        String displayName = ncUserDetailUpdateRequest.getDisplayName();
        String avatarId = ncUserDetailUpdateRequest.getAvatarId();

        // update non-critical details
        userDetailServiceImpl.updateNonCriticalDetails(userId, displayName, avatarId);
        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "5.3 Update one subject selected by the signed-in user",
            description = "")
    @PostMapping("/subjects")
    public UserSubjectUpdateResponse updateUserSubject(@CookieValue(value = "accessToken", required = false) String accessToken,
                                                       @Valid @RequestBody UserSubjectUpdateRequest userSubjectUpdateRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get update request info
        String subjectId = userSubjectUpdateRequest.getSubjectId();
        boolean isSelected = userSubjectUpdateRequest.getIsSelected();

        // update
        isSelected = userDetailServiceImpl.updateUserSubject(userId, subjectId, isSelected);

        return new UserSubjectUpdateResponse(ErrorCode.ERROR_CODE_OK, subjectId, isSelected);
    }

    @Operation(summary = "5.4 Badges of the signed-in user",
            description = "")
    @GetMapping("/badges")
    public UserBadgesResponse getUserBadges(@CookieValue(value = "accessToken", required = false) String accessToken)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get user badges
        List<UserBadgeDTO> userBadgeList = userDetailServiceImpl.getUserBadges(userId);

        return new UserBadgesResponse(ErrorCode.ERROR_CODE_OK, userBadgeList);
    }

    @Operation(summary = "5.5 Update displayed badges of the signed-in user",
            description = "")
    @PostMapping("/badges")
    public UserBadgeUpdateResponse UpdateUserBadge(@CookieValue(value = "accessToken", required = false) String accessToken,
                                                   @Valid @RequestBody UserBadgeUpdateRequest userBadgeUpdateRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get update request info
        String badgeId = userBadgeUpdateRequest.getBadgeId();
        boolean isDisplayed = userBadgeUpdateRequest.getIsDisplayed();

        // update
        isDisplayed = userDetailServiceImpl.updateUserBadge(userId, badgeId, isDisplayed);

        return new UserBadgeUpdateResponse(ErrorCode.ERROR_CODE_OK, badgeId, isDisplayed);
    }

    @Operation(summary = "5.6 Update password",
            description = "")
    @PostMapping("/chpasswd")
    public SimpleResponse UpdateUserPassword(@CookieValue(value = "accessToken", required = false) String accessToken,
                                             @Valid @RequestBody UserPasswordResetRequest userPasswordResetRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get update request info
        String currentPassword = userPasswordResetRequest.getCurrentPassword();
        String newPassword = userPasswordResetRequest.getNewPassword();

        // update
        userDetailServiceImpl.updateUserPassword(userId, currentPassword, newPassword);

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "5.6(1) Email update request",
            description = "")
    @PostMapping("/email-update-request")
    public SimpleResponse UpdateUserEmailRequest(@CookieValue(value = "accessToken", required = false) String accessToken,
                                                 @Valid @RequestBody UserEmailUpdateRequestRequest userEmailUpdateRequestRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get update request info
        String newEmail = userEmailUpdateRequestRequest.getNewEmail();

        // email request
        userDetailServiceImpl.updateUserEmailRequest(userId, newEmail);

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

    @Operation(summary = "5.7 Verify email update",
            description = "")
    @PostMapping("/email-update-verify")
    public SimpleResponse UpdateUserEmail(@CookieValue(value = "accessToken", required = false) String accessToken,
                                                 @Valid @RequestBody UserEmailUpdateRequest userEmailUpdateRequest)
            throws ApiException {
        // validate token
        Integer userId = userDetailServiceImpl.getUserIdByToken(accessToken);

        // get update request info
        String token = userEmailUpdateRequest.getToken();

        // email update
        userDetailServiceImpl.updateUserEmail(userId, token);

        return new SimpleResponse(ErrorCode.ERROR_CODE_OK);
    }

}
