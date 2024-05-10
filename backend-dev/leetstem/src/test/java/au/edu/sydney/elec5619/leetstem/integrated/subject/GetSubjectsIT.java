package au.edu.sydney.elec5619.leetstem.integrated.subject;

import au.edu.sydney.elec5619.leetstem.constant.ErrorCode;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.entity.UserSubject;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadSubjectIdException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetSubjectsIT extends SubjectIT {
    @Test
    void shouldReturnAllSubjectsWithoutLoggingIn() throws JsonProcessingException, BadSubjectIdException {
        ResponseEntity<String> response = restTemplate.getForEntity(endpointGetSubjects, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        assertTrue(body.get("is_none_selected").asBoolean());

        JsonNode actualSubjects = body.get("subjects");
        JsonNode actualSubject = actualSubjects.get(0);
        for (int idx = 0; actualSubject != null; ++idx, actualSubject = actualSubjects.get(idx)) {
            Subject subject = Subject.fromId(actualSubject.get("subject_id").asInt());
            assertEquals(subject.getName(), actualSubject.get("subject_name").asText());
            assertFalse(actualSubject.get("is_selected").asBoolean());
        }
    }
    @Test
    void shouldReturnSelectedSubjectsWhenLoggedIn() throws JsonProcessingException {
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

        // Get subjects
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpointGetSubjects, HttpMethod.GET, entity, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(ErrorCode.ERROR_CODE_OK.getCode(), body.get("error_code").asInt());
        assertFalse(body.get("is_none_selected").asBoolean());

        JsonNode actualSubjects = body.get("subjects");
        JsonNode actualSubject = actualSubjects.get(0);
        List<UserSubject> userSubjects = userSubjectRepository.findAllByUserId(1);
        for (int idx = 0; actualSubject != null; ++idx, actualSubject = actualSubjects.get(idx)) {
            JsonNode finalActualSubject = actualSubject;
            assertTrue(userSubjects.stream().anyMatch(userSubject ->
                    userSubject.getSubject() == finalActualSubject.get("subject_id").asInt()
                    && userSubject.isSelected() == finalActualSubject.get("is_selected").asBoolean()
            ));
        }
    }
}
