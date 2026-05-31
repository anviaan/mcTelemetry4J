package net.anvian.mctelemetry4j.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
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
    public ByteArrayOutputStream generateCsv() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(stream));
             Stream<Telemetry> telemetryStream = telemetryRepository.streamAll()) {
            writer.writeNext(new String[]{"mod_id", "game_version", "mod_version", "loader", "count"});

            telemetryStream.forEach(t ->
                    writer.writeNext(new String[]{
                            t.getMod().getModId(),
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
    public List<TelemetryResponse> generateJson() {
        try (Stream<Telemetry> telemetryStream = telemetryRepository.streamAll()) {
            return telemetryStream.map(t -> new TelemetryResponse(
                    t.getMod().getModId(),
                    t.getMod().getModName(),
                    t.getGameVersion(),
                    t.getModVersion(),
                    t.getLoader(),
                    t.getCount()
            )).collect(Collectors.toList());
        }
    }
}
