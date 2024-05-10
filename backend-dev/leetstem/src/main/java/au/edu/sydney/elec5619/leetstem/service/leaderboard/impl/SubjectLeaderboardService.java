package au.edu.sydney.elec5619.leetstem.service.leaderboard.impl;

import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingDTO;
import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingUser;
import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingUserBadge;
import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.entity.User;
import au.edu.sydney.elec5619.leetstem.entity.UserSubjectStats;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadSubjectIdException;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import au.edu.sydney.elec5619.leetstem.service.db.UserStatsService;
import au.edu.sydney.elec5619.leetstem.service.leaderboard.LeaderboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SubjectLeaderboardService implements LeaderboardService {

    private final UserStatsService userStatsService;
    private final UserService userService;

    public SubjectLeaderboardService(UserStatsService userStatsService, UserService userService) {
        this.userStatsService = userStatsService;
        this.userService = userService;
    }

    @Override
    public SubjectRankingDTO getSubjectRanking(Integer userId, String subjectId) throws ApiException {
        // check subjectId
        if (subjectId == null) {
            throw new BadSubjectIdException();
        }
        int subjectIdInt;
        try {
            subjectIdInt = Integer.parseInt(subjectId);
        } catch (NumberFormatException e) {
            throw new BadSubjectIdException();
        }
        Subject.fromId(subjectIdInt);

        // get rankings
        SubjectRankingDTO subjectRankingDTO = new SubjectRankingDTO();
        subjectRankingDTO.setSubjectId(subjectId);

        List<UserSubjectStats> rankings = userStatsService.getSubjectRanking(subjectIdInt);
        List<SubjectRankingUser> users = new ArrayList<>();

        // get current user info
        User currentUser = userService.getUserById(userId);
        if (currentUser != null) {
            subjectRankingDTO.setCurrentUserDisplayName(currentUser.getDisplayName());
            subjectRankingDTO.setCurrentUserImage(currentUser.getAvatar());
        }

        int rank = 1;
        // iterate users
        for (UserSubjectStats stats: rankings) {
            User user = userService.getUserById(stats.getUserId());

            SubjectRankingUser subjectRankingUser = new SubjectRankingUser();
            subjectRankingUser.setRank(rank);
            subjectRankingUser.setDisplayName(user.getDisplayName());
            subjectRankingUser.setUserImage(user.getAvatar());
            subjectRankingUser.setPoints(stats.getTotalCorrect());
            subjectRankingUser.setBadges(getRankingUserBadge(stats.getUserId()));

            users.add(subjectRankingUser);

            // check if current user
            if (Objects.equals(userId, stats.getUserId())) {
                subjectRankingDTO.setCurrentUserRank(rank);
                subjectRankingDTO.setCurrentUserPoints(stats.getTotalCorrect());
            }

            rank++;
        }

        subjectRankingDTO.setUsers(users);

        return subjectRankingDTO;
    }

    private List<SubjectRankingUserBadge> getRankingUserBadge(int userId) {
        List<SubjectRankingUserBadge> badges = new ArrayList<>();

        List<UserBadgeDTO> userBadgeDTOList = userService.getBadgesByUserId(userId);
        for (UserBadgeDTO userBadgeDTO : userBadgeDTOList) {
            if (userBadgeDTO.isDisplayed()) {
                SubjectRankingUserBadge badge = new SubjectRankingUserBadge();
                badge.setBadgeId(userBadgeDTO.getBadgeId());
                badge.setBadgeIcon(userBadgeDTO.getBadgeIcon());
                badge.setBadgeName(userBadgeDTO.getBadgeName());
                badges.add(badge);
            }
        }

        return badges;
    }
}
