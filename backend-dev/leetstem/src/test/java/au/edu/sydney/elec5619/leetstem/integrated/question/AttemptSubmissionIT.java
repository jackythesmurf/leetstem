package au.edu.sydney.elec5619.leetstem.integrated.question;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class AttemptSubmissionIT extends QuestionIT{
    @Test
    void shouldReturnErrorOnNonExistentQuestionID() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{ \"question_id\": \"123455\", \"attempt_body\": \"2\" }",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointAttemptSubmission, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_QUESTION_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnPassedOnCorrectAttemptBody() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{ \"question_id\": \"9448\", \"attempt_body\": \"{76}{55}{62}{29}{32}\" }",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointAttemptSubmission, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        assertTrue(body.get("passed").asBoolean());
    }
    @Test
    void shouldReturnNotPassedOnIncorrectAttemptBody() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{ \"question_id\": \"9448\", \"attempt_body\": \"{45}{1}{62}{3}{5}\" }",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointAttemptSubmission, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        assertFalse(body.get("passed").asBoolean());
    }
}
