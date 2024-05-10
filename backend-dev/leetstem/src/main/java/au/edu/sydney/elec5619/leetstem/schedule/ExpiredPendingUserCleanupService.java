package au.edu.sydney.elec5619.leetstem.schedule;

import au.edu.sydney.elec5619.leetstem.service.db.PendingUserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpiredPendingUserCleanupService {
    private final PendingUserService jpaPendingUserService;
    private final long EXPIRATION_SCAN_RATE_MILLIS = 1000 * 60 * 5;  // 5 minutes

    public ExpiredPendingUserCleanupService(PendingUserService jpaPendingUserService) {
        this.jpaPendingUserService = jpaPendingUserService;
    }

    @Scheduled(fixedRate = EXPIRATION_SCAN_RATE_MILLIS)
    public void removeExpiredPendingUserEntries() {
        jpaPendingUserService.removeExpiredPendingUser();
    }
}
