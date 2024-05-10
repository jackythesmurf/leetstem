package au.edu.sydney.elec5619.leetstem.service.subject.impl;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.dto.SubjectDetail;
import au.edu.sydney.elec5619.leetstem.entity.UserSubject;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadTokenException;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class JwtSubjectServiceTest {

    private StatefulJwtService mockedStatefulJwtService;
    private UserService mockedUserService;
    private JwtSubjectService jwtSubjectService;
    private Random random;

    @BeforeEach
    void setup() {
        mockedStatefulJwtService = Mockito.mock(StatefulJwtService.class);
        mockedUserService = Mockito.mock(UserService.class);
        jwtSubjectService = new JwtSubjectService(mockedStatefulJwtService, mockedUserService);

        random = new Random(5619);
    }

    @Nested
    class GetSubjectsByTokenTest {
        @Test
        void shouldReturnAllSubjectsUponNullToken() throws ApiException {
            // When calling getSubjectsByToken with null token,
            List<SubjectDetail> subjectDetails = jwtSubjectService.getSubjectsByToken(null);
            // Then all subjects should have been returned.
            for (Subject subject : Subject.values()) {
                assertTrue(subjectDetails
                        .stream()
                        .anyMatch(subjectDetail ->
                            subject.getId().toString().equals(subjectDetail.getSubjectId())
                        ));
            }
        }

        @Test
        void shouldThrowBadTokenExceptionOnInvalidTokens() {
            // Given a jwt service that rejects all tokens (treating all as invalid)
            when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                    any(),
                    any()
            )).thenThrow(MalformedJwtException.class);
            assertThrows(BadTokenException.class, () -> jwtSubjectService.getSubjectsByToken(""));
            assertThrows(BadTokenException.class, () -> jwtSubjectService.getSubjectsByToken("abc"));
        }

        @Test
        void shouldReturnSubjectsOfUsersBasedOnDatabase() throws ApiException {
            // Given a few users with randomly selected subjects
            int numUsers = 100;
            Map<Integer, Map<Subject, Boolean>> userIdToSubjectSelection = new HashMap<>();
            for (int __ = 0; __ < numUsers; ++__) {
                int userId = random.nextInt();
                Map<Subject, Boolean> subjectToSelection = new HashMap<>();
                for (Subject subject : Subject.values()) {
                    subjectToSelection.put(subject, random.nextInt() % 2 == 0);
                }
                userIdToSubjectSelection.put(userId, subjectToSelection);
            }
            // Given UserService wired to return the above data
            for (int userId : userIdToSubjectSelection.keySet()) {
                List<UserSubject> userSubjects = new ArrayList<>();
                Map<Subject, Boolean> subjectToSelection = userIdToSubjectSelection.get(userId);
                for (Subject subject : subjectToSelection.keySet()) {
                    UserSubject userSubject = new UserSubject();
                    userSubject.setUserId(userId);
                    userSubject.setSelected(subjectToSelection.get(subject));
                    userSubject.setSubject(subject.getId());
                    userSubjects.add(userSubject);
                }
                when(mockedUserService.getUserSubjectsByUserId(eq(userId)))
                        .thenReturn(userSubjects);
            }
            // Given a few tokens paired with each user
            Map<Integer, String> userIdToTokens = new HashMap<>();
            for (Integer userId : userIdToSubjectSelection.keySet()) {
                String token = String.valueOf(random.nextInt());
                userIdToTokens.put(userId, token);
                when(mockedStatefulJwtService.getSubjectIdIfAuthorisedTo(
                        eq(token),
                        eq(JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS)
                )).thenReturn(userId.toString());
            }

            // When calling getSubjectsByToken with each prepared user's token
            for (int userId : userIdToTokens.keySet()) {
                List<SubjectDetail> subjectDetails = jwtSubjectService.getSubjectsByToken(userIdToTokens.get(userId));
                // Then returned data should match prepared data for this user
                Map<Subject, Boolean> subjectToSelection = userIdToSubjectSelection.get(userId);
                for (Subject subject : subjectToSelection.keySet()) {
                    assertTrue(subjectDetails
                            .stream()
                            .anyMatch(subjectDetail -> subject.getId()
                                    .toString()
                                    .equals(subjectDetail.getSubjectId())
                                    && subjectToSelection.get(subject) == subjectDetail.isSelected()
                            ));
                }
            }
        }
    }
}