package net.anvian.mctelemetry4j.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ModPeriodStatsResponse(
        @Schema(example = "example-mod") String modId,
        @Schema(example = "Example Mod") String modName,
        @Schema(example = "2026-07") String period,
        @Schema(example = "42") Long totalCount
) {
}
