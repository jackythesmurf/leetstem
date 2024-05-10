package au.edu.sydney.elec5619.leetstem.service.subject.impl;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.constant.Topic;
import au.edu.sydney.elec5619.leetstem.dto.SubjectDetail;
import au.edu.sydney.elec5619.leetstem.dto.TopicListing;
import au.edu.sydney.elec5619.leetstem.entity.UserSubject;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;
import au.edu.sydney.elec5619.leetstem.exception.impl.BadTokenException;
import au.edu.sydney.elec5619.leetstem.service.db.UserService;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import au.edu.sydney.elec5619.leetstem.service.subject.SubjectService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtSubjectService implements SubjectService {
    private final StatefulJwtService simpleStatefulJwtService;
    private final UserService jpaUserService;

    public JwtSubjectService(StatefulJwtService simpleStatefulJwtService, UserService jpaUserService) {
        this.simpleStatefulJwtService = simpleStatefulJwtService;
        this.jpaUserService = jpaUserService;
    }
    @Override
    public List<SubjectDetail> getSubjectsByToken(String token) throws ApiException {
        List<SubjectDetail> subjectDetails = new ArrayList<>();
        // Return all subjects when no token is provided.
        if (null == token) {
            for (Subject subject : Subject.values()) {
                subjectDetails.add(new SubjectDetail(
                        subject.getId().toString(),
                        subject.getName(),
                        false));
            }
            return subjectDetails;
        }

        String userId;
        try {
            userId = simpleStatefulJwtService.getSubjectIdIfAuthorisedTo(token,
                    JwtAuthorisedAction.JWT_AUTHORISED_ACTION_ACCESS);
        } catch (UnsupportedJwtException |
                 MalformedJwtException |
                 ExpiredJwtException |
                 IllegalArgumentException e) {
            throw new BadTokenException();
        }

        List<UserSubject> userSubjects = jpaUserService.getUserSubjectsByUserId(Integer.parseInt(userId));
        for (UserSubject userSubject : userSubjects) {
            Subject subject = Subject.fromId(userSubject.getSubject());
            SubjectDetail subjectDetail = new SubjectDetail(
                    subject.getId().toString(),
                    subject.getName(),
                    userSubject.isSelected()
            );
            subjectDetails.add(subjectDetail);
        }
        return subjectDetails;
    }

    @Override
    public List<TopicListing> getTopicsBySubject(Subject subject) {
        List<TopicListing> topics = new ArrayList<>();
        for (Topic topic : Topic.values()) {
            if (topic.getSubject() == subject) {
                topics.add(new TopicListing(topic.getId(), topic.getName()));
            }
        }
        return topics;
    }
}
