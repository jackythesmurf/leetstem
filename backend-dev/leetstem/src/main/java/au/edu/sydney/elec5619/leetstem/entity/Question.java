package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer subject;

    private Integer topic;

    private Integer difficulty;

    private String body;

    private String answer;

    private String title;

    private Integer type;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}
