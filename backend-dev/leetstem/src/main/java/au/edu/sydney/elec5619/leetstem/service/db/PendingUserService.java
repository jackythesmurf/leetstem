package au.edu.sydney.elec5619.leetstem.service.db;

import au.edu.sydney.elec5619.leetstem.entity.PendingUser;

public interface PendingUserService {
    PendingUser getPendingUserById(int id);

    PendingUser insertPendingUser(String email, String digest);

    void removePendingUserById(int id);

    void removeExpiredPendingUser();
}
