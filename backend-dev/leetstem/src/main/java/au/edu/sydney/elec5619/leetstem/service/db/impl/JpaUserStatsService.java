package au.edu.sydney.elec5619.leetstem.service.db.impl;

import au.edu.sydney.elec5619.leetstem.entity.Badge;
import au.edu.sydney.elec5619.leetstem.entity.UserBadge;
import au.edu.sydney.elec5619.leetstem.entity.UserStats;
import au.edu.sydney.elec5619.leetstem.entity.UserSubjectStats;
import au.edu.sydney.elec5619.leetstem.repository.BadgeRepository;
import au.edu.sydney.elec5619.leetstem.repository.UserBadgeRepository;
import au.edu.sydney.elec5619.leetstem.repository.UserStatsRepository;
import au.edu.sydney.elec5619.leetstem.repository.UserSubjectStatsRepository;
import au.edu.sydney.elec5619.leetstem.service.db.UserStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JpaUserStatsService implements UserStatsService {
    private final UserSubjectStatsRepository jpaUserSubjectStatsRepository;
    private final UserStatsRepository jpaUserStatsRepository;

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;

    public JpaUserStatsService(UserSubjectStatsRepository jpaUserSubjectStatsRepository,
                               UserStatsRepository jpaUserStatsRepository,
                               UserBadgeRepository userBadgeRepository,
                               BadgeRepository badgeRepository) {
        this.jpaUserSubjectStatsRepository = jpaUserSubjectStatsRepository;
        this.jpaUserStatsRepository = jpaUserStatsRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
    }

    @Override
    public void incrementSubjectAttempt(int userId, int subjectId) {
        UserSubjectStats stats = jpaUserSubjectStatsRepository.findByUserIdAndSubject(userId, subjectId);
        if (null != stats) {
            stats.setTotalAttempted(stats.getTotalAttempted() + 1);
            jpaUserSubjectStatsRepository.save(stats);
        }
    }

    @Override
    public void incrementSubjectFirstCorrect(int userId, int subjectId) {
        UserSubjectStats stats = jpaUserSubjectStatsRepository.findByUserIdAndSubject(userId, subjectId);
        if (null != stats) {
            stats.setTotalCorrectFirst(stats.getTotalCorrectFirst() + 1);
            jpaUserSubjectStatsRepository.save(stats);
        }
    }

    @Override
    public void incrementSubjectTotalCorrect(int userId, int subjectId) {
        UserSubjectStats stats = jpaUserSubjectStatsRepository.findByUserIdAndSubject(userId, subjectId);
        if (null != stats) {
            stats.setTotalCorrect(stats.getTotalCorrect() + 1);
            jpaUserSubjectStatsRepository.save(stats);
        }
    }

    @Override
    public void incrementTotalAttempt(int userId) {
        UserStats stats = jpaUserStatsRepository.findByUserId(userId);
        if (null != stats) {
            stats.setTotalAttempted(stats.getTotalAttempted() + 1);
            jpaUserStatsRepository.save(stats);
        }
    }

    @Override
    public void incrementTotalCorrect(int userId) {
        UserStats stats = jpaUserStatsRepository.findByUserId(userId);
        if (null != stats) {
            stats.setTotalCorrect(stats.getTotalCorrect() + 1);
            jpaUserStatsRepository.save(stats);
        }
    }

    @Override
    public List<UserSubjectStats> getSubjectRanking(int subjectIdInt) {
        return jpaUserSubjectStatsRepository
                .findBySubjectAndTotalAttemptedGreaterThanOrderByTotalCorrectDesc(subjectIdInt, 0);
    }

    @Override
    public void earnBadge(int userId, int subjectId) {
        // get user subject stats
        UserSubjectStats stats = jpaUserSubjectStatsRepository.findByUserIdAndSubject(userId, subjectId);
        int totalCorrect = stats.getTotalCorrect();
        int totalCorrectFirst = stats.getTotalCorrectFirst();

        // check criteria
        String compCriteria = null;
        if (totalCorrect >= 10 && totalCorrect < 20) {
            compCriteria = "completion bronze";
        } else if (totalCorrect >= 20 && totalCorrect < 50) {
            compCriteria = "completion silver";
        } else if (totalCorrect >= 50 && totalCorrect < 100) {
            compCriteria = "completion gold";
        } else if (totalCorrect >= 100) {
            compCriteria = "completion diamond";
        }

        String execCriteria = null;
        if (totalCorrectFirst >= 10 && totalCorrectFirst < 20) {
            execCriteria = "excellence bronze";
        } else if (totalCorrectFirst >= 20 && totalCorrectFirst < 50) {
            execCriteria = "excellence silver";
        } else if (totalCorrectFirst >= 50 && totalCorrectFirst < 100) {
            execCriteria = "excellence gold";
        } else if (totalCorrectFirst >= 100) {
            execCriteria = "excellence diamond";
        }

        // get badges
        Badge compBadge = badgeRepository.findByNameAndSubject(compCriteria, subjectId);
        Badge execBadge = badgeRepository.findByNameAndSubject(execCriteria, subjectId);

        // check user's badge to avoid duplication
        if (compBadge != null && userBadgeRepository.findByUserIdAndBadgeId(userId, compBadge.getId()).isEmpty()) {
            UserBadge compUserBadge = new UserBadge();
            compUserBadge.setUserId(userId);
            compUserBadge.setBadgeId(compBadge.getId());
            compUserBadge.setIsDisplayed(false);
            userBadgeRepository.save(compUserBadge);
        }
        if (execBadge != null && userBadgeRepository.findByUserIdAndBadgeId(userId, execBadge.getId()).isEmpty()) {
            UserBadge execUserBadge = new UserBadge();
            execUserBadge.setUserId(userId);
            execUserBadge.setBadgeId(execBadge.getId());
            execUserBadge.setIsDisplayed(false);
            userBadgeRepository.save(execUserBadge);
        }
    }
}
