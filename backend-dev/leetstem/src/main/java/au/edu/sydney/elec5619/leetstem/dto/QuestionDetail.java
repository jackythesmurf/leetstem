package au.edu.sydney.elec5619.leetstem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionDetail {
    private String id;
    private boolean isAttempted;
    private int difficulty;
    private String title;
    private int type;
    private String body;
}
