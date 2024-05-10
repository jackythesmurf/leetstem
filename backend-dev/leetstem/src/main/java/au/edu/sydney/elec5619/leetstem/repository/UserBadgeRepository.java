package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {
    Optional<UserBadge> findByUserIdAndBadgeId(Integer userId, Integer badgeId);

    List<UserBadge> findByUserId(Integer userId);
}
