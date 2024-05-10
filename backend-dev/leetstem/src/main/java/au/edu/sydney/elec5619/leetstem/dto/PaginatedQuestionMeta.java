package au.edu.sydney.elec5619.leetstem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedQuestionMeta {
    private List<QuestionMeta> questions;
    private int pageSize;
    private int pageNo;
    private boolean isLastPage;
}
