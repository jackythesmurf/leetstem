package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Integer> {
    UserAuth findByEmail(String email);

    Optional<UserAuth> findByUserId(Integer userId);

    @Modifying
    @Query("UPDATE UserAuth userAuth SET userAuth.digest = ?2 WHERE userAuth.userId = ?1")
    void updateDigestByUserId(int userId, String digest);
}
