package net.anvian.mctelemetry4j.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.response.ModPeriodStatsResponse;
import net.anvian.mctelemetry4j.dto.response.TelemetryResponse;
import net.anvian.mctelemetry4j.exception.ExportExeption;
import net.anvian.mctelemetry4j.model.Telemetry;
import net.anvian.mctelemetry4j.repository.TelemetryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final TelemetryRepository telemetryRepository;

    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateCsv(String period) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(stream));
             Stream<Telemetry> telemetryStream = telemetryRepository.streamAll(period)) {
            writer.writeNext(new String[]{"mod_id", "period", "game_version", "mod_version", "loader", "count"});

            telemetryStream.forEach(t ->
                    writer.writeNext(new String[]{
                            t.getMod().getModId(),
                            t.getPeriod(),
                            t.getGameVersion(),
                            t.getModVersion(),
                            t.getLoader(),
                            t.getCount().toString()
                    })
            );
        } catch (IOException e) {
            throw new ExportExeption();
        }
        return stream;
    }

    @Transactional(readOnly = true)
    public List<TelemetryResponse> generateJson(String period) {
        try (Stream<Telemetry> telemetryStream = telemetryRepository.streamAll(period)) {
            return telemetryStream.map(t -> new TelemetryResponse(
                    t.getMod().getModId(),
                    t.getMod().getModName(),
                    t.getPeriod(),
                    t.getGameVersion(),
                    t.getModVersion(),
                    t.getLoader(),
                    t.getCount()
            )).collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<ModPeriodStatsResponse> generateStats(String period) {
        return telemetryRepository.aggregatedStats(period);
    }
}
