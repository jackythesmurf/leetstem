package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Integer> {
    PendingUser findById(int id);

    void deletePendingUsersByCreatedAtIsBefore(Timestamp expiredCreatedAt);
}
