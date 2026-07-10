package net.anvian.mctelemetry4j.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateModRequest(
        @Schema(description = "Stable Minecraft mod identifier", example = "example-mod", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String mod_id,
        @Schema(description = "Human-readable mod name", example = "Example Mod", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String mod_name
) {
}
