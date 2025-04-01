package net.anvian.mctelemetry4j.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DataRequest(
        @NotBlank String game_version,
        @NotBlank String mod_id,
        @NotBlank String mod_version,
        @NotBlank String loader
) {
}
