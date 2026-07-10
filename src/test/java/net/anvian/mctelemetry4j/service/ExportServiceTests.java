package net.anvian.mctelemetry4j.service;

import net.anvian.mctelemetry4j.repository.TelemetryRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExportServiceTests {

    private final TelemetryRepository telemetryRepository = mock(TelemetryRepository.class);
    private final ExportService exportService = new ExportService(telemetryRepository);

    @Test
    void findAvailablePeriodsPreservesRepositoryOrder() {
        List<String> periods = List.of("2026-07", "2026-06", "2026-05");
        when(telemetryRepository.findAvailablePeriods()).thenReturn(periods);

        assertThat(exportService.findAvailablePeriods()).containsExactlyElementsOf(periods);
        verify(telemetryRepository).findAvailablePeriods();
    }

    @Test
    void findAvailablePeriodsReturnsEmptyListWhenThereIsNoTelemetry() {
        when(telemetryRepository.findAvailablePeriods()).thenReturn(List.of());

        assertThat(exportService.findAvailablePeriods()).isEmpty();
        verify(telemetryRepository).findAvailablePeriods();
    }
}
