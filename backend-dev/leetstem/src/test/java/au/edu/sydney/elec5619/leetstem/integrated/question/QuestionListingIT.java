package au.edu.sydney.elec5619.leetstem.integrated.question;

import au.edu.sydney.elec5619.leetstem.constant.Difficulty;
import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionListingIT extends QuestionIT{
    @Test
    void shouldReturnErrorOnNonExistentSubjectID() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointListQuestions)
                .queryParam("subject_id", "112564987");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_SUBJECT_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorOnNonExistentTopicID() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointListQuestions)
                .queryParam("subject_id", Subject.PHYSICS.getId())
                .queryParam("topic_id", "7436809");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_TOPIC_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorOnNonExistentDifficulty() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointListQuestions)
                .queryParam("subject_id", Subject.PHYSICS.getId())
                .queryParam("topic_id", Topic.PHYSICS_TOPIC_1.getId())
                .queryParam("difficulty", "100182");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_DIFFICULTY.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorOnInvalidPaginationParams() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointListQuestions)
                .queryParam("subject_id", Subject.PHYSICS.getId())
                .queryParam("topic_id", Topic.PHYSICS_TOPIC_1.getId())
                .queryParam("difficulty", Difficulty.MEDIUM.getId())
                .queryParam("page_no", -1);
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_PAGINATION_PARAMETERS.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnSuccessOnListingQuestionsWithoutLoggedInUser() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointListQuestions)
                .queryParam("subject_id", Subject.PHYSICS.getId())
                .queryParam("topic_id", Topic.PHYSICS_TOPIC_1.getId())
                .queryParam("difficulty", Difficulty.MEDIUM.getId());
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
    }
}
