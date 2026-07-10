package net.anvian.mctelemetry4j.dto.response;

public record ModPeriodStatsResponse(
        String modId,
        String modName,
        String period,
        Long totalCount
) {
}
