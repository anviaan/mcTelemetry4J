package net.anvian.mctelemetry4j.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.response.TelemetryResponse;
import net.anvian.mctelemetry4j.exception.ExportExeption;
import net.anvian.mctelemetry4j.repository.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final TelemetryRepository telemetryRepository;

    public ByteArrayOutputStream generateCsv() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(stream))) {
            writer.writeNext(new String[]{"sep=,"});
            writer.writeNext(new String[]{"mod_id", "game_version", "mod_version", "loader", "count"});

            telemetryRepository.findAll().forEach(telemetry ->
                    writer.writeNext(new String[]{
                            telemetry.getMod().getModId(),
                            telemetry.getGameVersion(),
                            telemetry.getModVersion(),
                            telemetry.getLoader(),
                            telemetry.getCount().toString()
                    })
            );
        } catch (IOException e) {
            throw new ExportExeption();
        }
        return stream;
    }

    public List<TelemetryResponse> generateJson() {
        return telemetryRepository.findAll().stream()
                .map(t -> new TelemetryResponse(
                        t.getMod().getModId(),
                        t.getMod().getModName(),
                        t.getGameVersion(),
                        t.getModVersion(),
                        t.getLoader(),
                        t.getCount()
                ))
                .collect(Collectors.toList());
    }
}
