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
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SignUpRequestIT extends AuthIT {
    @Test
    void shouldReturnErrorUponMalformedEmails() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"not an email.com\", \"password\": \"some random password 12345\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignUpRequest, entity, String.class);
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

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignUpRequest, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_EMPTY_PASSWORD.getCode(), body.get("error_code").asInt());
    }

    @Test
    void shouldReturnSuccessUponValidEmailAndPassword() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                "{\"email\": \"user@leetstem.com\", \"password\": \"qwer1234\"}",
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(endpointSignUpRequest, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        // confirm that a code is indeed created in db
        assertFalse(pendingUserRepository.findAll().isEmpty());
    }
}
