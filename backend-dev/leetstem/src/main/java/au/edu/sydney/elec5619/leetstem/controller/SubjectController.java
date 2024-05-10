package au.edu.sydney.elec5619.leetstem.controller;

import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.dto.SubjectDetail;
import au.edu.sydney.elec5619.leetstem.dto.TopicListing;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.data.GetSubjectsResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.data.TopicListingResponse;
import au.edu.sydney.elec5619.leetstem.service.subject.SubjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data")
public class SubjectController {
    private final SubjectService jwtSubjectService;
    public SubjectController(SubjectService jwtSubjectService) {
        this.jwtSubjectService = jwtSubjectService;
    }

    @GetMapping("/subjects")
    public GetSubjectsResponse getSubjects(@CookieValue(value = "accessToken", required = false) String accessToken)
            throws ApiException {

        // By this line, `token` is either a string or null
        // A null `token` means no token was found.
        List<SubjectDetail> subjectDetails = jwtSubjectService.getSubjectsByToken(accessToken);
        GetSubjectsResponse response = new GetSubjectsResponse();
        response.setSubjectDetails(subjectDetails);
        response.setNoneSelected(true);
        if (subjectDetails.stream().anyMatch(SubjectDetail::isSelected)) {
            response.setNoneSelected(false);
        }

        return response;
    }

    @GetMapping("/topics")
    public TopicListingResponse getTopics(@RequestParam(name = "subject_id") Integer subjectId) throws ApiException {
        Subject subject = Subject.fromId(subjectId);
        List<TopicListing> topics = jwtSubjectService.getTopicsBySubject(subject);
        return new TopicListingResponse(topics);
    }
}
