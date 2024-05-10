package au.edu.sydney.elec5619.leetstem.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Role {
    STUDENT(0),
    TEACHER(1);

    private final Integer id;

    private static final Map<Integer, Role> idToRole = new HashMap<>();
    static {
        for (Role role : Role.values()) {
            idToRole.put(role.id, role);
        }
    }

    public static Role fromId(int id) {
        // Default to lowest privilege
        if (!idToRole.containsKey(id)) {
            return STUDENT;
        }
        return idToRole.get(id);
    }
}