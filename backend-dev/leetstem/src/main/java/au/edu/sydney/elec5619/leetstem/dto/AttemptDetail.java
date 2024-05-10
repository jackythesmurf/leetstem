package au.edu.sydney.elec5619.leetstem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AttemptDetail {
    private String body;
    private boolean correct;
}
