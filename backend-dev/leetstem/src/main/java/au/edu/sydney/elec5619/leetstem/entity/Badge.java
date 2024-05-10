package au.edu.sydney.elec5619.leetstem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "badge")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

    @Column(name = "subject", nullable = false)
    private Integer subject;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
