package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.UserSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubjectRepository extends JpaRepository<UserSubject, Integer> {
    List<UserSubject> findAllByUserId(int userId);

    Optional<UserSubject> findByUserIdAndSubject(Integer userId, Integer subject);
}
