package net.anvian.mctelemetry4j;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
public class McTelemetry4JApplication {

    static void main(String[] args) {
        SpringApplication.run(McTelemetry4JApplication.class, args);
    }

}
