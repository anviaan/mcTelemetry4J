package net.anvian.mctelemetry4j.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.exception.ExportExeption;
import net.anvian.mctelemetry4j.repository.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final TelemetryRepository telemetryRepository;

    public ByteArrayOutputStream generateCsv() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(stream))) {
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
}
