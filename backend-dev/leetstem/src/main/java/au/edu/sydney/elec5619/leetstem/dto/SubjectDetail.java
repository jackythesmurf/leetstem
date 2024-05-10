package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SubjectDetail {
    @JsonProperty("subject_id")
    @Setter
    String subjectId;
    @JsonProperty("subject_name")
    @Setter
    String subjectName;
    @JsonProperty("is_selected")
    @Setter
    boolean isSelected;
}
