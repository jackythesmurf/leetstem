package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("SELECT DISTINCT q FROM Question q " +
            "LEFT JOIN Attempt a ON q.id = a.questionId AND a.userId = :userId AND a.isCorrect = TRUE " +
            "WHERE " +
            "(:subject IS NULL OR q.subject = :subject) AND " +
            "(:topic IS NULL OR q.topic = :topic) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
            "((:userId IS NULL) OR (:passed IS NULL) OR " +
            "(:passed = TRUE AND a IS NOT NULL) OR " +
            "(:passed = FALSE AND a IS NULL) OR " +
            "(:passed IS NULL))")
    Page<Question> findByNullableFilters(@Param("subject") Integer subject,
                                         @Param("topic") Integer topic,
                                         @Param("difficulty") Integer difficulty,
                                         @Param("passed") Boolean passed,
                                         @Param("userId") Integer userId,
                                         Pageable pageable);

    Question findById(int questionId);
}
