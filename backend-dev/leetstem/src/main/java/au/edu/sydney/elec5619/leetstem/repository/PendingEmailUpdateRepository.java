package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.PendingEmailUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface PendingEmailUpdateRepository extends JpaRepository<PendingEmailUpdate, Integer> {
    Optional<PendingEmailUpdate> findByUserId(Integer userId);

    void deletePendingEmailsByCreatedAtIsBefore(Timestamp expiredCreatedAt);
}
