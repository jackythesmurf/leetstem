package au.edu.sydney.elec5619.leetstem.constant;

import au.edu.sydney.elec5619.leetstem.exception.impl.BadTopicIdException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Topic {
    /////////////////// Biology ///////////////////
    BIOLOGY_TOPIC_1(0, Subject.BIOLOGY, "Topic 1 of Biology"),
    BIOLOGY_TOPIC_2(1, Subject.BIOLOGY, "Topic 2 of Biology"),
    BIOLOGY_TOPIC_3(2, Subject.BIOLOGY, "Topic 3 of Biology"),
    BIOLOGY_TOPIC_4(3, Subject.BIOLOGY, "Topic 4 of Biology"),
    BIOLOGY_TOPIC_5(4, Subject.BIOLOGY, "Topic 5 of Biology"),
    /////////////////// Chemistry ///////////////////
    CHEMISTRY_TOPIC_1(0, Subject.CHEMISTRY, "Topic 1 of Chemistry"),
    CHEMISTRY_TOPIC_2(1, Subject.CHEMISTRY, "Topic 2 of Chemistry"),
    CHEMISTRY_TOPIC_3(2, Subject.CHEMISTRY, "Topic 3 of Chemistry"),
    /////////////////// Design and technology ///////////////////
    DESIGN_AND_TECHNOLOGY_TOPIC_1(0, Subject.DESIGN_AND_TECHNOLOGY, "Topic 1 of Design and Technology"),
    DESIGN_AND_TECHNOLOGY_TOPIC_2(1, Subject.DESIGN_AND_TECHNOLOGY, "Topic 2 of Design and Technology"),
    DESIGN_AND_TECHNOLOGY_TOPIC_3(2, Subject.DESIGN_AND_TECHNOLOGY, "Topic 3 of Design and Technology"),
    /////////////////// Engineering studies ///////////////////
    ENGINEERING_STUDIES_TOPIC_1(0, Subject.ENGINEERING_STUDIES, "Topic 1 of Engineering Studies"),
    ENGINEERING_STUDIES_TOPIC_2(1, Subject.ENGINEERING_STUDIES, "Topic 2 of Engineering Studies"),
    /////////////////// Engineering studies ///////////////////
    INFORMATION_PROCESSES_AND_TECHNOLOGY_TOPIC_1(0, Subject.INFORMATION_PROCESSES_AND_TECHNOLOGY, "Topic 1 of Information Processes and Technology"),
    INFORMATION_PROCESSES_AND_TECHNOLOGY_TOPIC_2(1, Subject.INFORMATION_PROCESSES_AND_TECHNOLOGY, "Topic 2 of Information Processes and Technology"),
    INFORMATION_PROCESSES_AND_TECHNOLOGY_TOPIC_3(2, Subject.INFORMATION_PROCESSES_AND_TECHNOLOGY, "Topic 3 of Information Processes and Technology"),
    INFORMATION_PROCESSES_AND_TECHNOLOGY_TOPIC_4(3, Subject.INFORMATION_PROCESSES_AND_TECHNOLOGY, "Topic 4 of Information Processes and Technology"),
    /////////////////// Maths standard 2 ///////////////////
    MATHEMATICS_STANDARD_2_TOPIC_1(0, Subject.MATHEMATICS_STANDARD_2, "Topic 1 of Mathematics Standard 2"),
    MATHEMATICS_STANDARD_2_TOPIC_2(1, Subject.MATHEMATICS_STANDARD_2, "Topic 2 of Mathematics Standard 2"),
    MATHEMATICS_STANDARD_2_TOPIC_3(2, Subject.MATHEMATICS_STANDARD_2, "Topic 3 of Mathematics Standard 2"),
    /////////////////// Maths advanced ///////////////////
    MATHEMATICS_ADVANCED_TOPIC_1(0, Subject.MATHEMATICS_ADVANCED, "Topic 1 of Mathematics Advanced"),
    MATHEMATICS_ADVANCED_TOPIC_2(1, Subject.MATHEMATICS_ADVANCED, "Topic 2 of Mathematics Advanced"),
    MATHEMATICS_ADVANCED_TOPIC_3(2, Subject.MATHEMATICS_ADVANCED, "Topic 3 of Mathematics Advanced"),
    /////////////////// Mathematics Extension 1 ///////////////////
    MATHEMATICS_EXTENSION_1_TOPIC_1(0, Subject.MATHEMATICS_EXTENSION_1, "Topic 1 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_2(1, Subject.MATHEMATICS_EXTENSION_1, "Topic 2 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_3(2, Subject.MATHEMATICS_EXTENSION_1, "Topic 3 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_4(3, Subject.MATHEMATICS_EXTENSION_1, "Topic 4 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_5(4, Subject.MATHEMATICS_EXTENSION_1, "Topic 5 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_6(5, Subject.MATHEMATICS_EXTENSION_1, "Topic 6 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_7(6, Subject.MATHEMATICS_EXTENSION_1, "Topic 7 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_8(7, Subject.MATHEMATICS_EXTENSION_1, "Topic 8 of Mathematics Extension 1"),
    MATHEMATICS_EXTENSION_1_TOPIC_9(8, Subject.MATHEMATICS_EXTENSION_1, "Topic 9 of Mathematics Extension 1"),
    /////////////////// Mathematics Extension 2 ///////////////////
    MATHEMATICS_EXTENSION_2_TOPIC_1(0, Subject.MATHEMATICS_EXTENSION_2, "Topic 1 of Mathematics Extension 2"),
    MATHEMATICS_EXTENSION_2_TOPIC_2(1, Subject.MATHEMATICS_EXTENSION_2, "Topic 2 of Mathematics Extension 2"),
    MATHEMATICS_EXTENSION_2_TOPIC_3(2, Subject.MATHEMATICS_EXTENSION_2, "Topic 3 of Mathematics Extension 2"),
    MATHEMATICS_EXTENSION_2_TOPIC_4(3, Subject.MATHEMATICS_EXTENSION_2, "Topic 4 of Mathematics Extension 2"),
    MATHEMATICS_EXTENSION_2_TOPIC_5(4, Subject.MATHEMATICS_EXTENSION_2, "Topic 5 of Mathematics Extension 2"),
    MATHEMATICS_EXTENSION_2_TOPIC_6(5, Subject.MATHEMATICS_EXTENSION_2, "Topic 6 of Mathematics Extension 2"),
    MATHEMATICS_EXTENSION_2_TOPIC_7(6, Subject.MATHEMATICS_EXTENSION_2, "Topic 7 of Mathematics Extension 2"),
    /////////////////// Physics ///////////////////
    PHYSICS_TOPIC_1(0, Subject.PHYSICS, "Topic 1 of Physics"),
    PHYSICS_TOPIC_2(1, Subject.PHYSICS, "Topic 2 of Physics"),
    PHYSICS_TOPIC_3(2, Subject.PHYSICS, "Topic 3 of Physics"),
    PHYSICS_TOPIC_4(3, Subject.PHYSICS, "Topic 4 of Physics"),
    PHYSICS_TOPIC_5(4, Subject.PHYSICS, "Topic 5 of Physics"),
    PHYSICS_TOPIC_6(5, Subject.PHYSICS, "Topic 6 of Physics"),
    /////////////////// Science Extension ///////////////////
    SCIENCE_EXTENSION_TOPIC_1(0, Subject.SCIENCE_EXTENSION, "Topic 1 of Science Extension"),
    SCIENCE_EXTENSION_TOPIC_2(1, Subject.SCIENCE_EXTENSION, "Topic 2 of Science Extension"),
    SCIENCE_EXTENSION_TOPIC_3(2, Subject.SCIENCE_EXTENSION, "Topic 3 of Science Extension"),
    /////////////////// Software Design and Development ///////////////////
    SOFTWARE_DESIGN_AND_DEVELOPMENT_TOPIC_1(0, Subject.SOFTWARE_DESIGN_AND_DEVELOPMENT, "Topic 1 of Software Design and Development"),
    SOFTWARE_DESIGN_AND_DEVELOPMENT_TOPIC_2(1, Subject.SOFTWARE_DESIGN_AND_DEVELOPMENT, "Topic 2 of Software Design and Development"),
    SOFTWARE_DESIGN_AND_DEVELOPMENT_TOPIC_3(2, Subject.SOFTWARE_DESIGN_AND_DEVELOPMENT, "Topic 3 of Software Design and Development"),
    SOFTWARE_DESIGN_AND_DEVELOPMENT_TOPIC_4(3, Subject.SOFTWARE_DESIGN_AND_DEVELOPMENT, "Topic 4 of Software Design and Development"),
    SOFTWARE_DESIGN_AND_DEVELOPMENT_TOPIC_5(4, Subject.SOFTWARE_DESIGN_AND_DEVELOPMENT, "Topic 5 of Software Design and Development"),
    SOFTWARE_DESIGN_AND_DEVELOPMENT_TOPIC_6(5, Subject.SOFTWARE_DESIGN_AND_DEVELOPMENT, "Topic 6 of Software Design and Development"),
    SOFTWARE_DESIGN_AND_DEVELOPMENT_TOPIC_7(6, Subject.SOFTWARE_DESIGN_AND_DEVELOPMENT, "Topic 7 of Software Design and Development"),
    /////////////////// Information and Digital Technology ///////////////////
    INFORMATION_AND_DIGITAL_TECHNOLOGY_TOPIC_1(0, Subject.INFORMATION_AND_DIGITAL_TECHNOLOGY, "Topic 1 of Information and Digital Technology"),
    INFORMATION_AND_DIGITAL_TECHNOLOGY_TOPIC_2(1, Subject.INFORMATION_AND_DIGITAL_TECHNOLOGY, "Topic 2 of Information and Digital Technology"),
    INFORMATION_AND_DIGITAL_TECHNOLOGY_TOPIC_3(2, Subject.INFORMATION_AND_DIGITAL_TECHNOLOGY, "Topic 3 of Information and Digital Technology"),
    INFORMATION_AND_DIGITAL_TECHNOLOGY_TOPIC_4(3, Subject.INFORMATION_AND_DIGITAL_TECHNOLOGY, "Topic 4 of Information and Digital Technology"),
    /////////////////// Mathematics Standard 1 ///////////////////
    MATHEMATICS_STANDARD_1_TOPIC_1(0, Subject.MATHEMATICS_STANDARD_1, "Topic 1 of IMathematics Standard 1"),
    MATHEMATICS_STANDARD_1_TOPIC_2(1, Subject.MATHEMATICS_STANDARD_1, "Topic 2 of IMathematics Standard 1"),
    MATHEMATICS_STANDARD_1_TOPIC_3(2, Subject.MATHEMATICS_STANDARD_1, "Topic 3 of IMathematics Standard 1"),
    MATHEMATICS_STANDARD_1_TOPIC_4(3, Subject.MATHEMATICS_STANDARD_1, "Topic 4 of IMathematics Standard 1");

    private final Integer id;
    private final Subject subject;
    private final String name;

    private static final Map<String, Topic> subjectAndIdToTopic = new HashMap<>();
    static {
        for (Topic topic : Topic.values()) {
            subjectAndIdToTopic.put(genKey(topic.subject, topic.id), topic);
        }
    }

    private static String genKey(Subject subject, int id) {
        return String.format("subject: {%d}, topic: {%d}", subject.getId(), id);
    }

    public static Topic fromSubjectAndTopicId(Subject subject, int id) throws BadTopicIdException {
        if (!subjectAndIdToTopic.containsKey(genKey(subject, id))) {
            throw new BadTopicIdException();
        }
        return subjectAndIdToTopic.get(genKey(subject, id));
    }
}