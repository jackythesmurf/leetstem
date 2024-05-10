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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PasswordResetRequestIT extends AuthIT {
    @Test
    void shouldReturnErrorUponMalformedEmails() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"not an email.com\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointPasswordResetRequest, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_MALFORMED_EMAIL.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnSuccessButDoNothingOnValidEmailButNotRegisteredEmails() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"someone@someplace.com\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointPasswordResetRequest, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        // But no email should have been sent
        verify(mockEmailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void shouldReturnSuccessAndSendEmailOnRegisteredEmails() throws JsonProcessingException {
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

        assertNotNull(emailTokenCaptor.getValue());
        assertEquals(email, emailTargetCaptor.getValue());
    }
}
