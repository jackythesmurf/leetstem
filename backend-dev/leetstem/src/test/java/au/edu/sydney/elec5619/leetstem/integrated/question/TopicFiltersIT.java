package au.edu.sydney.elec5619.leetstem.integrated.question;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadTopicIdException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopicFiltersIT extends QuestionIT{
    @Test
    void shouldReturnErrorOnNonExistentSubjectID() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointTopicFilters)
                .queryParam("subject_id", "112564987");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_SUBJECT_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnSuccessOnValidSubjectID() throws JsonProcessingException, BadTopicIdException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointTopicFilters)
                .queryParam("subject_id", Subject.MATHEMATICS_EXTENSION_2.getId());
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        JsonNode topics = body.get("topics");
        JsonNode actualTopic = topics.get(0);
        for (int idx = 0; actualTopic != null; ++idx, actualTopic = topics.get(idx)) {
            Topic topic = Topic.fromSubjectAndTopicId(Subject.MATHEMATICS_EXTENSION_2, actualTopic.get("topic_id").asInt());
            assertEquals(topic.getName(), actualTopic.get("topic_name").asText());
        }
    }
}
