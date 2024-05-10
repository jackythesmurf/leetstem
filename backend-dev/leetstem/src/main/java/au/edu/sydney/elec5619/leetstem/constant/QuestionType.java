package au.edu.sydney.elec5619.leetstem.constant;

import au.edu.sydney.elec5619.leetstem.exception.impl.BadQuestionTypeException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum QuestionType {
    MCQ(0),
    FITB(1);


    private final Integer id;

    private static final Map<Integer, QuestionType> idToQuestionType = new HashMap<>();
    static {
        for (QuestionType type : QuestionType.values()) {
            idToQuestionType.put(type.id, type);
        }
    }

    public static QuestionType fromId(int id) throws BadQuestionTypeException {
        if (!idToQuestionType.containsKey(id)) {
            throw new BadQuestionTypeException();
        }
        return idToQuestionType.get(id);
    }
}