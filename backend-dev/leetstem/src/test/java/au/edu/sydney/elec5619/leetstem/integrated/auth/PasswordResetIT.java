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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class PasswordResetIT extends AuthIT {
    @Test
    void shouldReturnErrorOnInvalidTokens() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"token\": \"no encrypted token looks like this\", \"new_password\": \"MyNewPassW0rD\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointPasswordReset, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_TOKEN.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnErrorOnInvalidPasswords() throws JsonProcessingException {
        // Get a token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String email = "s2@test.com";

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"" + email + "\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointPasswordResetRequest, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        verify(mockEmailService).sendPasswordResetEmail(emailTokenCaptor.capture(), emailTargetCaptor.capture());

        String token = emailTokenCaptor.getValue();
        System.out.println(token);

        // Make request with valid token
        entity = new HttpEntity<>(
                "{\"token\": \"" + token + "\", \"new_password\": \"\"}",
                headers
        );

        response = restTemplate.postForEntity(endpointPasswordReset, entity, String.class);
        body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_EMPTY_PASSWORD.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnSuccessOnValidTokenAndValidNewPassword() throws JsonProcessingException {
        // Get a token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String email = "s1@test.com";

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"" + email + "\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointPasswordResetRequest, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        verify(mockEmailService).sendPasswordResetEmail(emailTokenCaptor.capture(), emailTargetCaptor.capture());

        String token = emailTokenCaptor.getValue();

        String newPassword = "IWillNeverForgetThisPassword";

        // Make request with valid token
        entity = new HttpEntity<>(
                "{\"token\": \"" + token + "\", \"new_password\": \"" + newPassword + "\"}",
                headers
        );

        response = restTemplate.postForEntity(endpointPasswordReset, entity, String.class);
        body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        // And the account may be logged in with the new password
        entity = new HttpEntity<>(
                "{\"email\": \"s1@test.com\", \"password\": \"" + newPassword + "\"}",
                headers
        );

        response = restTemplate.postForEntity(endpointSignIn, entity, String.class);
        body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());

        int userId = userAuthRepository.findByEmail("s1@test.com").getUserId();
        User user = userRepository.findUserById(userId);
        assertEquals(user.getDisplayName(), body.get("display_name").asText());
        assertEquals(user.getAvatar(), body.get("avatar").asText());

        UserRole userRole = userRoleRepository.findByUserId(userId).get();
        assertEquals(userRole.getRole(), body.get("role").asInt());
    }
}
