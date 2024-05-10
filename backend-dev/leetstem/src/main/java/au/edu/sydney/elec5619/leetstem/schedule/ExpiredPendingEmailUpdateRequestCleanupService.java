package au.edu.sydney.elec5619.leetstem.schedule;

import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpiredPendingEmailUpdateRequestCleanupService {
    private final UserService jpaUserService;

    private final long EXPIRATION_SCAN_RATE_MILLIS = 1000 * 60 * 5;  // 5 minutes

    public ExpiredPendingEmailUpdateRequestCleanupService(UserService jpaUserService) {
        this.jpaUserService = jpaUserService;
    }

    @Scheduled(fixedRate = EXPIRATION_SCAN_RATE_MILLIS)
    public void removeExpiredPendingEmailUpdateRequestsEntries() {
        jpaUserService.removePendingEmailUpdate();
    }
}
