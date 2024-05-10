package au.edu.sydney.elec5619.leetstem.schedule;

import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpiredInvalidTokenCleanupService {
    private static final long EXPIRATION_SCAN_RATE_MILLIS = 1000 * 10;  // 10 seconds
    private final StatefulJwtService simpleStatefulJwtService;

    public ExpiredInvalidTokenCleanupService(StatefulJwtService simpleStatefulJwtService) {
        this.simpleStatefulJwtService = simpleStatefulJwtService;
    }

    @Scheduled(fixedRate = EXPIRATION_SCAN_RATE_MILLIS)
    public void removeExpiredInvalidTokens() {
        simpleStatefulJwtService.removeExpiredInvalidTokens();
    }
}
