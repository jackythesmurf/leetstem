package au.edu.sydney.elec5619.leetstem.integrated.comment;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentEndorsingIT extends CommentIT {
    @Test
    void shouldReturnErrorWithoutLoggedInUser() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"comment_id\": \"2\", \"endorsed\": true}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointCommentEndorsing, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_UNAUTHENTICATED_USER.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWithoutPrivilege() throws JsonProcessingException {
        // Sign in as a normal (student) user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"s1@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);

        // Get token set in cookie
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        String token = cookies.stream()
                .filter(cookie -> cookie.startsWith("accessToken"))
                .findFirst()
                .orElse(null);
        assertNotNull(token);
        headers.add(HttpHeaders.COOKIE, token);

        entity = new HttpEntity<>(
                "{\"comment_id\": \"2\", \"endorsed\": true}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentEndorsing, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_NO_PRIVILEGE.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWithNonExistentComment() throws JsonProcessingException {
        // Sign in
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"t1@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);

        // Get token set in cookie
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        String token = cookies.stream()
                .filter(cookie -> cookie.startsWith("accessToken"))
                .findFirst()
                .orElse(null);
        assertNotNull(token);
        headers.add(HttpHeaders.COOKIE, token);

        // Call with not attempted question
        entity = new HttpEntity<>(
                "{\"comment_id\": \"2334\", \"endorsed\": true}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentEndorsing, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_BAD_COMMENT_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldEndorseAndCancelEndorsingAComment() throws JsonProcessingException {
        // Sign in
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"t1@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);

        // Get token set in cookie
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        String token = cookies.stream()
                .filter(cookie -> cookie.startsWith("accessToken"))
                .findFirst()
                .orElse(null);
        assertNotNull(token);
        headers.add(HttpHeaders.COOKIE, token);

        // Endorse
        entity = new HttpEntity<>(
                "{\"comment_id\": \"1\", \"endorsed\": true}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentEndorsing, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        assertTrue(commentRepository
                .findByQuestionId(9448, PageRequest.of(0,1))
                .getContent()
                .get(0).isEndorsed());

        // Cancel endorsement
        entity = new HttpEntity<>(
                "{\"comment_id\": \"1\", \"endorsed\": false}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentEndorsing, entity, String.class);
        body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        assertFalse(commentRepository
                .findByQuestionId(9448, PageRequest.of(0,1))
                .getContent()
                .get(0).isEndorsed());
    }
}
