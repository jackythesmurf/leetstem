package au.edu.sydney.elec5619.leetstem.integrated.comment;

import au.edu.sydney.elec5619.leetstem.integrated.BaseIT;
import au.edu.sydney.elec5619.leetstem.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommentIT extends BaseIT {
    @Autowired
    protected CommentRepository commentRepository;
}
