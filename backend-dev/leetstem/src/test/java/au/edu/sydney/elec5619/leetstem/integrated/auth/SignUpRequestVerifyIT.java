package au.edu.sydney.elec5619.leetstem.integrated.auth;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class SignUpRequestVerifyIT extends AuthIT {
    @Test
    void shouldReturnErrorOnAnyInvalidToken() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"token\": \"abcdefg1234567\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignUpVerify, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_BAD_TOKEN.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnSuccessOnValidToken() throws JsonProcessingException {
        // Create a signup request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String email = "some.user@leetstem.com";

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"" + email + "\", \"password\": \"qwer1234\"}",
                headers
        );
        restTemplate.postForEntity(endpointSignUpRequest, entity, String.class);

        // Get the token
        verify(mockEmailService).sendSignUpVerificationEmail(emailTokenCaptor.capture(), emailTargetCaptor.capture());
        String token = emailTokenCaptor.getValue();
        String target = emailTargetCaptor.getValue();
        assertEquals(email, target);

        // Verify
        entity = new HttpEntity<>(
                "{\"token\": \"" + token + "\"}",
                headers
        );
        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignUpVerify, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
    }
}
