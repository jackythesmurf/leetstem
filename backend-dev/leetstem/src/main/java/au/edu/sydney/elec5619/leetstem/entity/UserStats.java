package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "total_endorsed")
    private Integer totalEndorsed;

    @Column(name = "total_attempted")
    private Integer totalAttempted;

    @Column(name = "total_correct")
    private Integer totalCorrect;
}
