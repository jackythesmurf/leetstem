package au.edu.sydney.elec5619.leetstem.constant;

import au.edu.sydney.elec5619.leetstem.exception.impl.BadDifficultyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Difficulty {
    EASY(0),
    MEDIUM(1),
    HARD(2);


    private final Integer id;

    private static final Map<Integer, Difficulty> idToDifficulty = new HashMap<>();
    static {
        for (Difficulty difficulty : Difficulty.values()) {
            idToDifficulty.put(difficulty.id, difficulty);
        }
    }

    public static Difficulty fromId(int id) throws BadDifficultyException {
        if (!idToDifficulty.containsKey(id)) {
            throw new BadDifficultyException();
        }
        return idToDifficulty.get(id);
    }
}