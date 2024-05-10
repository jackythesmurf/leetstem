package au.edu.sydney.elec5619.leetstem.service.leaderboard.impl;


import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingDTO;
import au.edu.sydney.elec5619.leetstem.dto.UserBadgeDTO;
import au.edu.sydney.elec5619.leetstem.entity.User;
import au.edu.sydney.elec5619.leetstem.entity.UserSubjectStats;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadSubjectIdException;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import au.edu.sydney.elec5619.leetstem.service.db.UserStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SubjectLeaderboardServiceTest {

    private SubjectLeaderboardService subjectLeaderboardService;

    private UserStatsService userStatsServiceMocked;
    private UserService userServiceMocked;

    @BeforeEach
    public void setup() {
        // mocks
        userStatsServiceMocked = Mockito.mock(UserStatsService.class);
        userServiceMocked = Mockito.mock(UserService.class);

        // setup
        subjectLeaderboardService = new SubjectLeaderboardService(userStatsServiceMocked, userServiceMocked);
    }

    @Nested
    class getSubjectRankingTest {
        @Test
        public void exceptionTest() {
            // null subjectId
            assertThrows(BadSubjectIdException.class,
                    () -> subjectLeaderboardService.getSubjectRanking(1, null));

            // subjectId format error
            assertThrows(BadSubjectIdException.class,
                    () -> subjectLeaderboardService.getSubjectRanking(1, "not number"));

            // invalid subjectId
            assertThrows(BadSubjectIdException.class,
                    () -> subjectLeaderboardService.getSubjectRanking(1, "-1"));
        }

        @Test
        public void normalTest() throws ApiException {
            // setup
            UserSubjectStats userSubjectStats1 = new UserSubjectStats();
            userSubjectStats1.setUserId(1);
            userSubjectStats1.setTotalCorrect(2);
            UserSubjectStats userSubjectStats2 = new UserSubjectStats();
            userSubjectStats2.setUserId(2);
            userSubjectStats2.setTotalCorrect(1);

            List<UserSubjectStats> userSubjectStatsList = new ArrayList<>();
            userSubjectStatsList.add(userSubjectStats1);
            userSubjectStatsList.add(userSubjectStats2);

            User user1 = new User();
            user1.setId(1);
            User user2 = new User();
            user2.setId(2);

            UserBadgeDTO userBadgeDTO1 = new UserBadgeDTO();
            userBadgeDTO1.setDisplayed(true);
            UserBadgeDTO userBadgeDTO2 = new UserBadgeDTO();
            userBadgeDTO2.setDisplayed(false);
            List<UserBadgeDTO> userBadgeDTOList = new ArrayList<>();
            userBadgeDTOList.add(userBadgeDTO1);
            userBadgeDTOList.add(userBadgeDTO2);

            when(userStatsServiceMocked.getSubjectRanking(eq(15030))).thenReturn(userSubjectStatsList);
            when(userServiceMocked.getUserById(eq(1))).thenReturn(user1);
            when(userServiceMocked.getUserById(eq(2))).thenReturn(user2);
            when(userServiceMocked.getBadgesByUserId(eq(1))).thenReturn(userBadgeDTOList);

            // ranking details
            SubjectRankingDTO subjectRankingDTO = subjectLeaderboardService.getSubjectRanking(1, "15030");
            assertEquals(1, subjectRankingDTO.getCurrentUserRank());
            assertEquals(2, subjectRankingDTO.getUsers().size());
            assertEquals(1, subjectRankingDTO.getUsers().get(0).getRank());
            assertEquals(2, subjectRankingDTO.getUsers().get(1).getRank());
        }
    }
}
