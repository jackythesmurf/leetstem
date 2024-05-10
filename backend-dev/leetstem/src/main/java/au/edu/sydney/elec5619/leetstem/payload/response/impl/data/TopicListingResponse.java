package au.edu.sydney.elec5619.leetstem.payload.response.impl.data;

import au.edu.sydney.elec5619.leetstem.dto.TopicListing;
import au.edu.sydney.elec5619.leetstem.payload.response.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TopicListingResponse extends BaseResponse {
    @JsonProperty("topics")
    private List<TopicListing> topics;
}
