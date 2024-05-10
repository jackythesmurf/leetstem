package au.edu.sydney.elec5619.leetstem.integrated.comment;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentPostingIT extends CommentIT {
    @Test
    void shouldReturnErrorWithoutLoggedInUser() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"question_id\": 9448, \"comment_type\": 0, \"comment_body\": \"I love this question\"}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointCommentPosting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_UNAUTHENTICATED_USER.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWhenSignedInUserHasNoAttempts() throws JsonProcessingException {
        // Sign in
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

        // Call with not attempted question
        entity = new HttpEntity<>(
                "{\"question_id\": \"9450\", \"comment_type\": \"0\", \"comment_body\": \"I love this question\"}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentPosting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_NO_ATTEMPT.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWithNonExistentContentType() throws JsonProcessingException {
        // Sign in
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

        // Call with not attempted question
        entity = new HttpEntity<>(
                "{\"question_id\": \"9448\", \"comment_type\": \"7\", \"comment_body\": \"I love this question\"}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentPosting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_BAD_COMMENT_TYPE.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWhenBadWordsInComment() throws JsonProcessingException {
        // Sign in
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

        // Call with not attempted question
        entity = new HttpEntity<>(
                "{\"question_id\": \"9448\", \"comment_type\": \"0\", \"comment_body\": \"shitty question\"}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentPosting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_BAD_CONTENT.getCode(), body.get("error_code").asInt());
    }
}
