package au.edu.sydney.elec5619.leetstem.controller;


import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingDTO;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.leaderboard.SubjectRankingResponse;
import au.edu.sydney.elec5619.leetstem.service.leaderboard.LeaderboardService;
import au.edu.sydney.elec5619.leetstem.service.user.UserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/stats")
@RestController
@Tag(name = "6.Leaderboard", description = "Endpoints of ranking")
public class LeaderboardController {

    private final UserDetailService userDetailService;
    private final LeaderboardService leaderboardService;

    public LeaderboardController(UserDetailService userDetailService,
                                 LeaderboardService leaderboardService) {
        this.userDetailService = userDetailService;
        this.leaderboardService = leaderboardService;
    }

    @Operation(summary = "6.1 Get the leaderboard of a subject",
            description = "")
    @GetMapping("/leaderboard")
    public SubjectRankingResponse getSubjectLeaderboard(@CookieValue(value = "accessToken", required = false) String accessToken,
                                                        @RequestParam(name = "subject_id") String subjectId)
            throws ApiException {

        // get current user
        Integer userId = null;
        try {
            userId = userDetailService.getUserIdByToken(accessToken);
        } catch (Exception e) {
            // do nothing
        }

        // get subject ranking
        SubjectRankingDTO subjectRankingDTO = leaderboardService.getSubjectRanking(userId, subjectId);

        return new SubjectRankingResponse(ErrorCode.ERROR_CODE_OK, subjectRankingDTO);
    }
}
