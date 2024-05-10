package au.edu.sydney.elec5619.leetstem.payload.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserSubjectUpdateRequest {
    @JsonProperty("subject_id")
    private String subjectId;

    @JsonProperty("is_selected")
    private Boolean isSelected;
}
