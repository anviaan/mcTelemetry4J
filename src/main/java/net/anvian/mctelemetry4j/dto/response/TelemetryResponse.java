package net.anvian.mctelemetry4j.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TelemetryResponse(
        @Schema(example = "example-mod") String modId,
        @Schema(example = "Example Mod") String modName,
        @Schema(example = "2026-07") String period,
        @Schema(example = "1.21.1") String gameVersion,
        @Schema(example = "1.2.0") String modVersion,
        @Schema(example = "fabric") String loader,
        @Schema(example = "42") Long count
) {
}
