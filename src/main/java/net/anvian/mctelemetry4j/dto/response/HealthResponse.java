package net.anvian.mctelemetry4j.dto.response;

import java.time.LocalDateTime;

public record HealthResponse(
        String status,
        LocalDateTime timestamp,
        String database
) {
}
