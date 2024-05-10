package au.edu.sydney.elec5619.leetstem.service.subject;

import au.edu.sydney.elec5619.leetstem.constant.Subject;
import au.edu.sydney.elec5619.leetstem.dto.SubjectDetail;
import au.edu.sydney.elec5619.leetstem.dto.TopicListing;
import au.edu.sydney.elec5619.leetstem.exception.ApiException;

import java.util.List;

public interface SubjectService {
    List<SubjectDetail> getSubjectsByToken(String token) throws ApiException;
    List<TopicListing> getTopicsBySubject(Subject subject) throws ApiException;
}
