package karki.com.karki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "karki.com.karki.entity")
@EnableJpaRepositories(basePackages = "karki.com.karki.repository")
public class KarkiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KarkiApplication.class, args);
    }

}
