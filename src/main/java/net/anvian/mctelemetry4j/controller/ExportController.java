package net.anvian.mctelemetry4j.controller;

import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.service.ExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {
    private final ExportService exportService;

    @GetMapping("/csv")
    public ResponseEntity<InputStreamResource> exportToCsv() {

        ByteArrayOutputStream stream = exportService.generateCsv();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(stream.toByteArray()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=telemetry_data.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}
