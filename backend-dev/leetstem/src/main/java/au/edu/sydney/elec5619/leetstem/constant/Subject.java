package au.edu.sydney.elec5619.leetstem.constant;

import au.edu.sydney.elec5619.leetstem.exception.impl.BadSubjectIdException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Subject {
    BIOLOGY(15030, "Biology"),
    CHEMISTRY(15050, "Chemistry"),
    DESIGN_AND_TECHNOLOGY(15080, "Design and Technology"),
    ENGINEERING_STUDIES(15120, "Engineering Studies"),
    INFORMATION_PROCESSES_AND_TECHNOLOGY(15210, "Information Processes and Technology"),
    MATHEMATICS_STANDARD_2(15236, "Mathematics Standard 2"),
    MATHEMATICS_ADVANCED(15255, "Mathematics Advanced"),
    MATHEMATICS_EXTENSION_1(15250, "Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_2(15260, "Mathematics Extension 2"),
    PHYSICS(15330, "Physics"),
    SCIENCE_EXTENSION(15345, "Science Extension"),
    SOFTWARE_DESIGN_AND_DEVELOPMENT(15360, "Software Design and Development"),
    INFORMATION_AND_DIGITAL_TECHNOLOGY(27398, "Information and Digital Technology"),
    MATHEMATICS_STANDARD_1(15232, "Mathematics Standard 1");


    private final Integer id;
    private final String name;

    private static final Map<Integer, Subject> idToSubject = new HashMap<>();
    static {
        for (Subject subject : Subject.values()) {
            idToSubject.put(subject.id, subject);
        }
    }

    public static Subject fromId(int id) throws BadSubjectIdException {
        if (!idToSubject.containsKey(id)) {
            throw new BadSubjectIdException();
        }
        return idToSubject.get(id);
    }
}