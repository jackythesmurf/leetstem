package au.edu.sydney.elec5619.leetstem.integrated;

import au.edu.sydney.elec5619.leetstem.service.email.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
public abstract class BaseIT {
    @Autowired
    protected TestRestTemplate restTemplate;
    @MockBean(name = "asyncJavaMailEmailService")
    protected EmailService mockEmailService;
    @Captor
    protected ArgumentCaptor<String> emailTokenCaptor;
    @Captor
    protected ArgumentCaptor<String> emailTargetCaptor;
    @LocalServerPort
    private int port;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected String endpointSignIn;
    protected String endpointSignUpRequest;
    protected String endpointSignUpVerify;
    protected String endpointPasswordResetRequest;
    protected String endpointPasswordReset;
    protected String endpointSignOut;

    protected String endpointGetSubjects;

    protected String endpointListQuestions;
    protected String endpointTopicFilters;
    protected String endpointQuestionDetails;
    protected String endpointAttemptSubmission;
    protected String endpointAttemptFetching;

    protected String endpointCommentListing;
    protected String endpointCommentPosting;
    protected String endpointCommentVoting;
    protected String endpointCommentEndorsing;
    protected String endpointCommentDeleting;

    @PostConstruct
    void setup() {
        String baseUri = "http://localhost:" + port + "/api";

        endpointSignIn = baseUri + "/auth/signin";
        endpointSignUpRequest = baseUri + "/auth/signup-request";
        endpointSignUpVerify = baseUri + "/auth/signup-verify";
        endpointPasswordResetRequest = baseUri + "/auth/passwd-reset-request";
        endpointPasswordReset = baseUri + "/auth/passwd-reset";
        endpointSignOut = baseUri + "/auth/signout";

        endpointGetSubjects = baseUri + "/data/subjects";

        endpointListQuestions = baseUri + "/data/questions/list";
        endpointTopicFilters = baseUri + "/data/topics";
        endpointQuestionDetails = baseUri + "/data/questions/details";
        endpointAttemptSubmission = baseUri + "/data/questions/attempt";
        endpointAttemptFetching = baseUri + "/data/questions/attempt";

        endpointCommentListing = baseUri + "/data/comments/list";
        endpointCommentPosting = baseUri + "/data/comments/post";
        endpointCommentVoting = baseUri + "/data/comments/vote";
        endpointCommentEndorsing = baseUri + "/data/comments/endorse";
        endpointCommentDeleting = baseUri + "/data/comments/delete";
    }
}
