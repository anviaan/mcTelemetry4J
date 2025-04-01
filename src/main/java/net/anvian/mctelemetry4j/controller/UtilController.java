package net.anvian.mctelemetry4j.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.anvian.mctelemetry4j.dto.response.HealthResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/")
public class UtilController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public ResponseEntity<byte[]> index() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            if (inputStream == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found".getBytes());
            }
            byte[] content = inputStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/html");
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error".getBytes());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        String databaseStatus = entityManager.isOpen() ? "connected" : "disconnected";
        return ResponseEntity.ok(
                new HealthResponse("healthy", LocalDateTime.now(ZoneOffset.UTC), databaseStatus)
        );
    }
}
