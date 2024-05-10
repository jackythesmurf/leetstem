package au.edu.sydney.elec5619.leetstem.service.db.impl;

import au.edu.sydney.elec5619.leetstem.entity.PendingUser;
import au.edu.sydney.elec5619.leetstem.repository.PendingUserRepository;
import au.edu.sydney.elec5619.leetstem.service.db.PendingUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@Transactional
public class JpaPendingUserService implements PendingUserService {
    private final PendingUserRepository pendingUserRepository;
    private final long signupRequestExpirationMillis;

    public JpaPendingUserService(PendingUserRepository pendingUserRepository,
                                 @Value("${leetstem.expiration.signUpRequest}") long signupRequestExpirationMillis) {
        this.pendingUserRepository = pendingUserRepository;
        this.signupRequestExpirationMillis = signupRequestExpirationMillis;
    }

    @Override
    public PendingUser getPendingUserById(int id) {
        return pendingUserRepository.findById(id);
    }

    @Override
    public PendingUser insertPendingUser(String email, String digest) {
        PendingUser pendingUser = new PendingUser();
        pendingUser.setEmail(email);
        pendingUser.setDigest(digest);
        return pendingUserRepository.save(pendingUser);
    }

    @Override
    public void removePendingUserById(int id) {
        if (pendingUserRepository.existsById(id)) {
            pendingUserRepository.deleteById(id);
        }
    }

    @Override
    public void removeExpiredPendingUser() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - signupRequestExpirationMillis);
        pendingUserRepository.deletePendingUsersByCreatedAtIsBefore(timestamp);
    }
}
