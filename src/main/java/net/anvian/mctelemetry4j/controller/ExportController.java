package net.anvian.mctelemetry4j.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
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
public class ExportController {
    private final ExportService exportService;

    @GetMapping("/csv")
    public ResponseEntity<InputStreamResource> exportToCsv(@RequestParam(required = false) @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String period) {

        ByteArrayOutputStream stream = exportService.generateCsv(period);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(stream.toByteArray()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=telemetry_data.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @GetMapping("/json")
    public ResponseEntity<List<TelemetryResponse>> exportToJson(@RequestParam(required = false) @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String period) {
        return ResponseEntity.ok().body(exportService.generateJson(period));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ModPeriodStatsResponse>> stats(@RequestParam(required = false) @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String period) {
        return ResponseEntity.ok(exportService.generateStats(period));
    }
}
