package au.edu.sydney.elec5619.leetstem.integrated.question;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionDetailsIT extends QuestionIT{
    @Test
    void shouldReturnErrorOnNonExistentQuestionID() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointQuestionDetails)
                .queryParam("question_id", "55543534");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_QUESTION_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnSuccessOnValidQuestionID() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointQuestionDetails)
                .queryParam("question_id", "9448");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
    }
}
