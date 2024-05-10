package au.edu.sydney.elec5619.leetstem.payload.response.impl.leaderboard;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingDTO;
import au.edu.sydney.elec5619.leetstem.dto.SubjectRankingUser;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubjectRankingResponse extends BaseResponse {

    @JsonProperty("subject_id")
    private String subjectId;

    @JsonProperty("current_user_rank")
    private int currentUserRank;

    @JsonProperty("current_user_image")
    private String currentUserImage;

    @JsonProperty("current_user_points")
    private int currentUserPoints;

    @JsonProperty("current_user_display_name")
    private String currentUserDisplayName;

    @JsonProperty("users")
    private List<SubjectRankingUser> users;

    public SubjectRankingResponse(ErrorCode errorCode, SubjectRankingDTO subjectRankingDTO) {
        this.errorCode = errorCode.getCode();
        this.subjectId = subjectRankingDTO.getSubjectId();
        this.currentUserRank = subjectRankingDTO.getCurrentUserRank();
        this.currentUserImage = subjectRankingDTO.getCurrentUserImage();
        this.currentUserPoints = subjectRankingDTO.getCurrentUserPoints();
        this.currentUserDisplayName = subjectRankingDTO.getCurrentUserDisplayName();
        this.users = subjectRankingDTO.getUsers();
    }
}
