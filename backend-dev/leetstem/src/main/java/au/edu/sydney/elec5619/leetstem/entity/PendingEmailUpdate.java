package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "pending_email_update")
public class PendingEmailUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

}
