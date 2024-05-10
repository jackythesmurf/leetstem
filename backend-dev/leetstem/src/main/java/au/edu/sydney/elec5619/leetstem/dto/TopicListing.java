package au.edu.sydney.elec5619.leetstem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopicListing {
    @JsonProperty("topic_id")
    private Integer topicId;

    @JsonProperty("topic_name")
    private String topicName;
}
