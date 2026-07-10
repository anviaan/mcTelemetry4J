package net.anvian.mctelemetry4j.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DataRequest(
        @Schema(description = "Minecraft game version", example = "1.21.1", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String game_version,
        @Schema(description = "Registered mod identifier", example = "example-mod", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String mod_id,
        @Schema(description = "Version of the reporting mod", example = "1.2.0", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String mod_version,
        @Schema(description = "Mod loader", example = "fabric", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String loader
) {
}
