package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "commenter_id")
    private Integer commenterId;

    @Column(name = "question_id")
    private Integer questionId;

    private String content;

    @Column(name = "content_type")
    private Integer contentType;

    @Column(name = "is_endorsed")
    private boolean isEndorsed;

    @Column(name = "total_votes")
    private Integer totalVotes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "commenter_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User commenter;
}
