package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedComment {
    @JsonProperty("page_no")
    private int pageNo;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("is_last_page")
    private boolean isLastPage;

    @JsonProperty("comments")
    List<CommentDTO> commentDTOList;
}
