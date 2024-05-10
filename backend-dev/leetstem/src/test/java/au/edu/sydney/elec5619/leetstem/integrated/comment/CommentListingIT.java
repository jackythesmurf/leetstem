package au.edu.sydney.elec5619.leetstem.integrated.comment;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.entity.Comment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentListingIT extends CommentIT {
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

        headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, token);

        // Get comments
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpointCommentListing)
                .queryParam("question_id", "9450");
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_NO_ATTEMPT.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnCommentsOfQuestion() throws JsonProcessingException {
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

        // Attempt the question
        entity = new HttpEntity<>(
                "{ \"question_id\": \"9450\", \"attempt_body\": \"{123}\" }",
                headers
        );
        restTemplate.postForEntity(endpointAttemptSubmission, entity, String.class);

        // Get comments
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointCommentListing)
                .queryParam("question_id", "9450");
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        Page<Comment> commentPage = commentRepository.findByQuestionId(9450, PageRequest.of(0, 10));
        JsonNode comments = body.get("comments");
        JsonNode actualComment = comments.get(0);
        for (int idx = 0; actualComment != null; ++idx, actualComment = comments.get(idx)) {
            JsonNode finalActualComment = actualComment;
            assertTrue(commentPage.stream().anyMatch(
                    comment -> comment.getCommenter().getId() == finalActualComment.get("commenter_id").asInt()
                    && comment.getId() == finalActualComment.get("comment_id").asInt()
                    && comment.isEndorsed() == finalActualComment.get("is_endorsed").asBoolean()
                    && comment.getContentType() == finalActualComment.get("comment_type").asInt()
                    && comment.getContent().equals(finalActualComment.get("comment_body").asText())
                    && comment.getTotalVotes() == finalActualComment.get("likes").asInt()
            ));
        }
    }
}
