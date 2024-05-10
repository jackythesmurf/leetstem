package au.edu.sydney.elec5619.leetstem.integrated.question;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AttemptFetchingIT extends QuestionIT{
    @Test
    void shouldReturnErrorOnNonExistentQuestionID() throws JsonProcessingException {
        // Get a valid token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"s1@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

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
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointAttemptFetching)
                .queryParam("question_id", "55543534");
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
        body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_QUESTION_ID.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorOnNotLoggedInRequests() throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointAttemptFetching)
                .queryParam("question_id", "9448");
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_UNAUTHENTICATED_USER.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnErrorWhenNoAttemptsMadeByLoggedInUser() throws JsonProcessingException {
        // Get a valid token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"s1@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

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
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointAttemptFetching)
                .queryParam("question_id", "9450");
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
        body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_NO_ATTEMPT.getCode(), body.get("error_code").asInt());
    }
    @Test
    void shouldReturnLastMadeAttempt() throws JsonProcessingException {
        // Get a valid token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"s1@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        // Get token set in cookie
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        String token = cookies.stream()
                .filter(cookie -> cookie.startsWith("accessToken"))
                .findFirst()
                .orElse(null);
        assertNotNull(token);

        headers.add(HttpHeaders.COOKIE, token);

        // Make an attempt
        String attempt = "{124512}";

        entity = new HttpEntity<>(
                "{ \"question_id\": \"9448\", \"attempt_body\": \"" + attempt + "\" }",
                headers
        );
        restTemplate.postForEntity(endpointAttemptSubmission, entity, String.class);

        // Fetch the attempt

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(endpointAttemptFetching)
                .queryParam("question_id", "9448");
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
        body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        assertEquals(attempt, body.get("attempt_body").asText());
    }
}
