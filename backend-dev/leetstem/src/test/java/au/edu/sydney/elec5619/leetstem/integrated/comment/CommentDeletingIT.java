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

public class CommentDeletingIT extends CommentIT {
    @Test
    void shouldReturnErrorWithoutLoggedInUser() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"comment_id\": \"2\"}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointCommentDeleting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_UNAUTHENTICATED_USER.getCode(), body.get("error_code").asInt());
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
                "{\"comment_id\": \"2442\"}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentDeleting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_BAD_COMMENT_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldDeleteComment() throws JsonProcessingException {
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

        // Delete
        entity = new HttpEntity<>(
                "{\"comment_id\": \"1\"}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentDeleting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        assertTrue(commentRepository
                .findByQuestionId(9448, PageRequest.of(0,1))
                .isEmpty());
    }
}
