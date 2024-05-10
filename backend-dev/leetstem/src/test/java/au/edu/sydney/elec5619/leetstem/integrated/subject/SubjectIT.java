package au.edu.sydney.elec5619.leetstem.integrated.subject;

import au.edu.sydney.elec5619.leetstem.integrated.BaseIT;
import au.edu.sydney.elec5619.leetstem.repository.UserSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;

abstract class SubjectIT extends BaseIT {
    @Autowired
    protected UserSubjectRepository userSubjectRepository;
}
