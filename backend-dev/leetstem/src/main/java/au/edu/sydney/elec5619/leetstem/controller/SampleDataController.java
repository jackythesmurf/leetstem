package au.edu.sydney.elec5619.leetstem.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/temp/sample")
@RestController
public class SampleDataController {

    @GetMapping("/**")
    public ResponseEntity<Resource> provideImage() {
        Resource image = new ClassPathResource("images/sample.image.png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }
}
