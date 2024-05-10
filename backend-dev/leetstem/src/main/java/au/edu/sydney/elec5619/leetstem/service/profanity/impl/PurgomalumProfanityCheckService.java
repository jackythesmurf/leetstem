package au.edu.sydney.elec5619.leetstem.service.profanity.impl;

import au.edu.sydney.elec5619.leetstem.service.profanity.ProfanityCheckService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PurgomalumProfanityCheckService implements ProfanityCheckService {

    public PurgomalumProfanityCheckService() {
    }

    @Override
    public boolean containProfanity(String text) {
        String apiUrl = "https://www.purgomalum.com/service/containsprofanity";
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("text", text)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);

        return Boolean.parseBoolean(result);
    }
}
