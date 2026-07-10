package net.anvian.mctelemetry4j.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.config.OpenApiConfig;
import net.anvian.mctelemetry4j.dto.response.ModPeriodStatsResponse;
import net.anvian.mctelemetry4j.dto.response.TelemetryResponse;
import net.anvian.mctelemetry4j.service.ExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping({"/export", "/telemetry/export"})
@RequiredArgsConstructor
@Validated
@Tag(name = "Exports", description = "Administrator-only telemetry exports and aggregate statistics.")
@SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME)
public class ExportController {
    private final ExportService exportService;

    @GetMapping("/csv")
    @Operation(summary = "Download telemetry as CSV")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "CSV file", content = @Content(mediaType = "text/csv")), @ApiResponse(responseCode = "400", description = "Period is invalid"), @ApiResponse(responseCode = "401", description = "Authentication required"), @ApiResponse(responseCode = "500", description = "Export failed")})
    public ResponseEntity<InputStreamResource> exportToCsv(@Parameter(description = "UTC month to export; omit for all retained periods", example = "2026-07", schema = @Schema(pattern = "\\d{4}-(0[1-9]|1[0-2])")) @RequestParam(required = false) @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String period) {
        ByteArrayOutputStream stream = exportService.generateCsv(period);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(stream.toByteArray()));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=telemetry_data.csv").contentType(MediaType.parseMediaType("text/csv")).body(resource);
    }

    @GetMapping("/json")
    @Operation(summary = "Export detailed telemetry as JSON")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Telemetry records", content = @Content(schema = @Schema(implementation = TelemetryResponse.class))), @ApiResponse(responseCode = "400", description = "Period is invalid"), @ApiResponse(responseCode = "401", description = "Authentication required"), @ApiResponse(responseCode = "500", description = "Export failed")})
    public ResponseEntity<List<TelemetryResponse>> exportToJson(@Parameter(description = "UTC month to export; omit for all retained periods", example = "2026-07", schema = @Schema(pattern = "\\d{4}-(0[1-9]|1[0-2])")) @RequestParam(required = false) @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String period) {
        return ResponseEntity.ok(exportService.generateJson(period));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get telemetry counts by mod and period")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Aggregated telemetry counts", content = @Content(schema = @Schema(implementation = ModPeriodStatsResponse.class))), @ApiResponse(responseCode = "400", description = "Period is invalid"), @ApiResponse(responseCode = "401", description = "Authentication required"), @ApiResponse(responseCode = "500", description = "Export failed")})
    public ResponseEntity<List<ModPeriodStatsResponse>> stats(@Parameter(description = "UTC month to export; omit for all retained periods", example = "2026-07", schema = @Schema(pattern = "\\d{4}-(0[1-9]|1[0-2])")) @RequestParam(required = false) @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String period) {
        return ResponseEntity.ok(exportService.generateStats(period));
    }

    @GetMapping("/periods")
    @Operation(summary = "List available telemetry periods")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Available periods in descending order", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(type = "string", pattern = "\\d{4}-(0[1-9]|1[0-2])", example = "2026-07"), arraySchema = @Schema(example = "[\"2026-07\", \"2026-06\", \"2026-05\"]")))), @ApiResponse(responseCode = "401", description = "Authentication required"), @ApiResponse(responseCode = "500", description = "Query failed")})
    public ResponseEntity<List<String>> periods() {
        return ResponseEntity.ok(exportService.findAvailablePeriods());
    }
}
