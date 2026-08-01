package net.anvian.mctelemetry4j.controller;

import net.anvian.mctelemetry4j.service.ExportService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportControllerTests {

    private final ExportService exportService = mock(ExportService.class);
    private final ExportController exportController = new ExportController(exportService);

    @Test
    void periodsReturnsAvailablePeriodsInServiceOrder() {
        List<String> periods = List.of("2026-07", "2026-06", "2026-05");
        when(exportService.findAvailablePeriods()).thenReturn(periods);

        ResponseEntity<List<String>> response = exportController.periods();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyElementsOf(periods);
        verify(exportService).findAvailablePeriods();
    }

    @Test
    void periodsReturnsEmptyArrayWhenThereIsNoTelemetry() {
        when(exportService.findAvailablePeriods()).thenReturn(List.of());

        ResponseEntity<List<String>> response = exportController.periods();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(exportService).findAvailablePeriods();
    }

    @Test
    void csvReturnsBytesWithDownloadHeaders() {
        byte[] csv = "mod_id,period,count\nexample-mod,2026-07,42\n".getBytes(StandardCharsets.UTF_8);
        when(exportService.generateCsv("2026-07")).thenReturn(csv);

        ResponseEntity<byte[]> response = exportController.exportToCsv("2026-07");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=telemetry_data.csv");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType("text/csv"));
        assertThat(response.getBody()).isEqualTo(csv);
        verify(exportService).generateCsv("2026-07");
    }
}
