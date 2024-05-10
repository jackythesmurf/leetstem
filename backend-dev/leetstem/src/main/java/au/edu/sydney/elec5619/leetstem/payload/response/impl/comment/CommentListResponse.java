package au.edu.sydney.elec5619.leetstem.payload.response.impl.comment;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.dto.CommentDTO;
import au.edu.sydney.elec5619.leetstem.dto.PaginatedComment;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommentListResponse extends BaseResponse {
    @JsonProperty("page_no")
    private int pageNo;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("is_last_page")
    private boolean isLastPage;

    @JsonProperty("comments")
    List<CommentDTO> commentDTOList;

    public CommentListResponse(ErrorCode errorCode, PaginatedComment paginatedComment) {
        this.errorCode = errorCode.getCode();
        this.pageNo = paginatedComment.getPageNo();
        this.pageSize = paginatedComment.getPageSize();
        this.isLastPage = paginatedComment.isLastPage();
        this.commentDTOList = paginatedComment.getCommentDTOList();
    }
}
