package net.anvian.mctelemetry4j.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.anvian.mctelemetry4j.dto.response.HealthResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@Tag(name = "Utility", description = "Service health and privacy information.")
public class UtilController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping(value = {"/telemetry", "/telemetry/"}, produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "View the privacy notice")
    @ApiResponse(responseCode = "200", description = "Privacy notice HTML", content = @Content(mediaType = "text/html"))
    public Resource index() {
        return new ClassPathResource("static/index.html");
    }

    @GetMapping({"/health", "/telemetry/health"})
    @Operation(summary = "Check service health")
    @ApiResponse(responseCode = "200", description = "Service and database status", content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    public ResponseEntity<HealthResponse> health() {
        String databaseStatus;
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            databaseStatus = "connected";
        } catch (Exception e) {
            databaseStatus = "disconnected";
        }
        return ResponseEntity.ok(
                new HealthResponse("healthy", LocalDateTime.now(ZoneOffset.UTC), databaseStatus)
        );
    }
}
