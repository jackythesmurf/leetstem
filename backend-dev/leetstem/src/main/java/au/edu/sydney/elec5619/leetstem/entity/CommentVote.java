package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "comment_vote")
public class CommentVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "voter_id")
    private Integer voterId;

    @Column(name = "comment_id")
    private Integer commentId;

    private Integer direction;

    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;
}
