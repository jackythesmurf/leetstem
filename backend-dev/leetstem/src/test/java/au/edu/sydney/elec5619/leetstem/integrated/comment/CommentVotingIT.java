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

public class CommentVotingIT extends CommentIT {
    @Test
    void shouldReturnErrorWithoutLoggedInUser() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"comment_id\": \"2\", \"direction\": 1}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointCommentVoting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_UNAUTHENTICATED_USER.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWithoutAttempts() throws JsonProcessingException {
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
                "{\"comment_id\": \"2\", \"direction\": 1}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentVoting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_NO_ATTEMPT.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWithNonExistentComment() throws JsonProcessingException {
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
                "{\"comment_id\": \"2442\", \"direction\": 1}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentVoting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_BAD_COMMENT_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldUpvoteAndDownVoteAndCancelVotingAComment() throws JsonProcessingException {
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

        int votesBefore = commentRepository
                .findByQuestionId(9448, PageRequest.of(0,1))
                .getContent()
                .get(0).getTotalVotes();

        // Upvote
        entity = new HttpEntity<>(
                "{\"comment_id\": \"1\", \"direction\": 1}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentVoting, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        int votesAfter = commentRepository
                .findByQuestionId(9448, PageRequest.of(0,1))
                .getContent()
                .get(0).getTotalVotes();
        assertTrue(votesBefore < votesAfter);

        // Down vote
        votesBefore = votesAfter;
        entity = new HttpEntity<>(
                "{\"comment_id\": \"1\", \"direction\": -11}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentVoting, entity, String.class);
        body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        votesAfter = commentRepository
                .findByQuestionId(9448, PageRequest.of(0,1))
                .getContent()
                .get(0).getTotalVotes();
        assertTrue(votesBefore > votesAfter);

        // Cancel vote (cancelled down vote so votes should go up after cancelling)
        votesBefore = votesAfter;
        entity = new HttpEntity<>(
                "{\"comment_id\": \"1\", \"direction\": 0}",
                headers
        );
        response = restTemplate.postForEntity(endpointCommentVoting, entity, String.class);
        body = objectMapper.readTree(response.getBody());
        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        votesAfter = commentRepository
                .findByQuestionId(9448, PageRequest.of(0,1))
                .getContent()
                .get(0).getTotalVotes();
        assertTrue(votesBefore < votesAfter);
    }
}
