package net.anvian.mctelemetry4j.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateModRequest(
        @NotBlank String mod_id,
        @NotBlank String mod_name
) {
}
