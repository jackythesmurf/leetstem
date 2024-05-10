package au.edu.sydney.elec5619.leetstem.integrated.auth;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.entity.User;
import au.edu.sydney.elec5619.leetstem.entity.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SignInIT extends AuthIT {

    @Test
    void shouldReturnErrorUponMismatchedCredentials() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"s1@test.com\", \"password\": \"some random password 12345\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_AUTHENTICATION_FAILED.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnErrorUponMalformedEmails() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"not an email.com\", \"password\": \"some random password 12345\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_MALFORMED_EMAIL.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnErrorUponEmptyPasswords() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"someone@someplace.com\", \"password\": \"\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_EMPTY_PASSWORD.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnSuccessWithBasicInformationUponMatchedCredentials() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"s1@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        int userId = userAuthRepository.findByEmail("s1@test.com").getUserId();
        User user = userRepository.findUserById(userId);
        assertEquals(user.getDisplayName(), body.get("display_name").asText());
        assertEquals(user.getAvatar(), body.get("avatar").asText());

        UserRole userRole = userRoleRepository.findByUserId(userId).get();
        assertEquals(userRole.getRole(), body.get("role").asInt());

        // And token is set in cookie
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        assertFalse(cookies.isEmpty());
        assertTrue(cookies.stream().anyMatch(cookie -> cookie.startsWith("accessToken")));
    }
}
