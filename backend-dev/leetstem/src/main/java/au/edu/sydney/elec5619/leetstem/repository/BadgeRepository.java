package au.edu.sydney.elec5619.leetstem.repository;

import au.edu.sydney.elec5619.leetstem.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    Badge findByNameAndSubject(String compCriteria, int subjectId);
}
