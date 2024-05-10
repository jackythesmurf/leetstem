package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.UserSubjectStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubjectStatsRepository extends JpaRepository<UserSubjectStats, Integer> {
    UserSubjectStats findByUserIdAndSubject(int userId, int subjectId);

    List<UserSubjectStats> findBySubjectAndTotalAttemptedGreaterThanOrderByTotalCorrectDesc(Integer subjectId, int totalAttempted);
}
