package au.edu.sydney.elec5619.leetstem.controller;

import au.edu.sydney.elec5619.leetstem.constant.Difficulty;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import au.edu.sydney.elec5619.leetstem.dto.AttemptDetail;
import au.edu.sydney.elec5619.leetstem.dto.PaginatedQuestionMeta;
import au.edu.sydney.elec5619.leetstem.dto.QuestionDetail;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.payload.request.data.AttemptSubmissionRequest;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.data.AttemptDetailResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.data.AttemptSubmissionResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.data.QuestionDetailResponse;
import au.edu.sydney.elec5619.leetstem.payload.response.impl.data.QuestionListingResponse;
import au.edu.sydney.elec5619.leetstem.service.question.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data/questions")
public class QuestionController {
    private final QuestionService jwtQuestionService;
    public QuestionController(QuestionService jwtQuestionService) {
        this.jwtQuestionService = jwtQuestionService;
    }

    @GetMapping("/list")
    public QuestionListingResponse getQuestions(@RequestParam(name = "subject_id") Integer subjectId,
                                               @RequestParam(name = "topic_id", required = false) Integer topicId,
                                               @RequestParam(name = "difficulty", required = false) Integer diff,
                                               @RequestParam(name = "passed", required = false) Boolean passed,
                                               @RequestParam(name = "page_no", required = false) Integer pageNo,
                                               @RequestParam(name = "page_size", required = false) Integer pageSize,
                                               @CookieValue(name = "accessToken", required = false) String token)
            throws ApiException {
        Subject subject = Subject.fromId(subjectId);
        Topic topic = topicId == null ? null : Topic.fromSubjectAndTopicId(subject, topicId);
        Difficulty difficulty = diff == null ? null : Difficulty.fromId(diff);
        PaginatedQuestionMeta paginatedQuestionMeta = jwtQuestionService
                .getPaginatedQuestionMetaFilteredBy(subject,topic, difficulty,
                        token, passed,
                        pageSize, pageNo);
        return new QuestionListingResponse(
                paginatedQuestionMeta.getPageNo(),
                paginatedQuestionMeta.getPageSize(),
                paginatedQuestionMeta.isLastPage(),
                passed != null && passed,
                subject.getId().toString(),
                topic == null ? null : topic.getId().toString(),
                difficulty == null ? null : difficulty.getId(),
                paginatedQuestionMeta.getQuestions());
    }

    @GetMapping("/details")
    public QuestionDetailResponse getQuestion(@RequestParam(name = "question_id") Integer questionId,
                                              @CookieValue(name = "accessToken", required = false) String token)
            throws ApiException {
        QuestionDetail detail = jwtQuestionService.getQuestionById(questionId, token);
        return new QuestionDetailResponse(
                detail.getId(),
                detail.isAttempted(),
                detail.getDifficulty(),
                detail.getTitle(),
                detail.getType(),
                detail.getBody()
        );
    }

    @PostMapping("/attempt")
    public AttemptSubmissionResponse makeAttempt(@RequestBody AttemptSubmissionRequest request,
                                                 @CookieValue(name = "accessToken", required = false) String token)
            throws ApiException {
        return new AttemptSubmissionResponse(
                jwtQuestionService.attempt(
                        Integer.parseInt(request.getQuestionId()),
                        request.getBody(),
                        token)
        );
    }

    @GetMapping("/attempt")
    public AttemptDetailResponse getAttempt(@RequestParam(name = "question_id") Integer questionId,
                                            @CookieValue(name = "accessToken", required = false) String token)
            throws ApiException {
        AttemptDetail detail = jwtQuestionService.getLatestAttemptByQuestionId(token, questionId);
        return new AttemptDetailResponse(
                detail.getBody(),
                detail.isCorrect()
        );
    }
}
