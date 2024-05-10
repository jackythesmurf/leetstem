package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
    List<Attempt> findByUserIdAndQuestionId(Integer userId, Integer questionId);

    @Query("SELECT a FROM Attempt a WHERE a.userId = ?1 AND a.questionId = ?2 AND a.isCorrect = ?3")
    List<Attempt> findByUserIdAndQuestionIdAndIsCorrect(int userId, int questionId, boolean isCorrect);

    Attempt findFirstByUserIdAndQuestionIdOrderByCreatedAtDesc(int userId, int questionId);
}
