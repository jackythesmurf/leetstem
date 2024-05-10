package au.edu.sydney.elec5619.leetstem.payload.response.impl.data;

import au.edu.sydney.elec5619.leetstem.dto.QuestionMeta;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class QuestionListingResponse extends BaseResponse {
    @JsonProperty("page_no")
    private int pageNo;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("is_last_page")
    private boolean isLastPage;

    @JsonProperty("passed")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean passed;

    @JsonProperty("subject_id")
    private String subjectId;

    @JsonProperty("topic_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String topicId;

    @JsonProperty("difficulty")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer difficulty;

    @JsonProperty("questions")
    private List<QuestionMeta> questions;
}
