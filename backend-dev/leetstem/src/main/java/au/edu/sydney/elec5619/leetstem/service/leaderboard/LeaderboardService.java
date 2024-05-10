package au.edu.sydney.elec5619.leetstem.service.leaderboard;

import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingDTO;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;

public interface LeaderboardService {
    SubjectRankingDTO getSubjectRanking(Integer userId, String subjectId) throws ApiException;
}
