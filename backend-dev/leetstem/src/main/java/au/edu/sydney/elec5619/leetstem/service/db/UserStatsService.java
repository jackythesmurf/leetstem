package au.edu.sydney.elec5619.leetstem.service.db;

import au.edu.sydney.elec5619.leetstem.entity.UserSubjectStats;

import java.util.List;

public interface UserStatsService {
    void incrementSubjectAttempt(int userId, int subjectId);

    void incrementSubjectFirstCorrect(int userId, int subjectId);

    void incrementSubjectTotalCorrect(int userId, int subjectId);

    void incrementTotalAttempt(int userId);

    void incrementTotalCorrect(int userId);

    List<UserSubjectStats> getSubjectRanking(int subjectIdInt);

    void earnBadge(int userId, int subjectId);
}
