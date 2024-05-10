package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_subject_stats")
public class UserSubjectStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private Integer subject;

    @Column(name = "total_attempted")
    private Integer totalAttempted;

    @Column(name = "total_correct")
    private Integer totalCorrect;

    @Column(name = "total_correct_first")
    private Integer totalCorrectFirst;
}
