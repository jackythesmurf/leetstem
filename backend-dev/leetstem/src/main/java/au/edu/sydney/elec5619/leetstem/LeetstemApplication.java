package au.edu.sydney.elec5619.leetstem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeetstemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeetstemApplication.class, args);
    }
}
