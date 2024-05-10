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

public class SignOutIT extends AuthIT {
    @Test
    void shouldReturnSuccessEvenNotSignedIn() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignOut, null, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnSuccessAndRemoveTokenWhenSignedIn() throws JsonProcessingException {
        // Sign in first
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"s2@test.com\", \"password\": \"Password123!@#\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        int userId = userAuthRepository.findByEmail("s2@test.com").getUserId();
        User user = userRepository.findUserById(userId);
        assertEquals(user.getDisplayName(), body.get("display_name").asText());
        assertEquals(user.getAvatar(), body.get("avatar").asText());

        UserRole userRole = userRoleRepository.findByUserId(userId).get();
        assertEquals(userRole.getRole(), body.get("role").asInt());

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

        // Request signout with token
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.postForEntity(endpointSignOut, entity, String.class);
        body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        // And cookie is set to be deleted
        cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        assertFalse(cookies.isEmpty());
        token = cookies.stream()
                .filter(cookie -> cookie.startsWith("accessToken"))
                .findFirst()
                .orElse(null);
        assertNotNull(token);
        assertTrue(token.contains("Max-Age=0"));
    }
}
