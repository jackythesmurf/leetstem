package au.edu.sydney.elec5619.leetstem.integrated.auth;

import au.edu.sydney.elec5619.leetstem.integrated.BaseIT;
import au.edu.sydney.elec5619.leetstem.repository.PendingUserRepository;
import au.edu.sydney.elec5619.leetstem.repository.UserAuthRepository;
import au.edu.sydney.elec5619.leetstem.repository.UserRepository;
import au.edu.sydney.elec5619.leetstem.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

abstract class AuthIT extends BaseIT {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserAuthRepository userAuthRepository;
    @Autowired
    protected UserRoleRepository userRoleRepository;
    @Autowired
    protected PendingUserRepository pendingUserRepository;
}
