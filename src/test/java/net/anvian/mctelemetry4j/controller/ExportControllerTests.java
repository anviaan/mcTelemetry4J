package net.anvian.mctelemetry4j.controller;

import net.anvian.mctelemetry4j.service.ExportService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}
