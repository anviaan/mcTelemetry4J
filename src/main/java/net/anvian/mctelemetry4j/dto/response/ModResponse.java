package net.anvian.mctelemetry4j.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ModResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "example-mod") String mod_id,
        @Schema(example = "Example Mod") String mod_name
) {
}
