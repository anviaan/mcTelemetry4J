package net.anvian.mctelemetry4j.dto.response;

public record TelemetryResponse(
        String modId,
        String modName,
        String period,
        String gameVersion,
        String modVersion,
        String loader,
        Long count
) {
}
