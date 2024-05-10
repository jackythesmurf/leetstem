package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "display_name")
    private String displayName;

    private String avatar;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
}
