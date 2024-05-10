package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Integer> {
    UserStats findByUserId(int userId);
}
