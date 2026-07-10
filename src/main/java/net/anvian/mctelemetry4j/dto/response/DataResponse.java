package net.anvian.mctelemetry4j.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DataResponse(
        @Schema(example = "Data received successfully") String message
) {
}
