package net.anvian.mctelemetry4j.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record HealthResponse(
        @Schema(example = "healthy") String status,
        @Schema(description = "UTC timestamp", example = "2026-07-09T12:00:00") LocalDateTime timestamp,
        @Schema(example = "connected") String database
) {
}
