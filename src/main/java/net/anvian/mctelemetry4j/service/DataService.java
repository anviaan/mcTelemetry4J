package net.anvian.mctelemetry4j.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.request.DataRequest;
import net.anvian.mctelemetry4j.dto.response.DataResponse;
import net.anvian.mctelemetry4j.exception.ModNotFoundException;
import net.anvian.mctelemetry4j.model.McMod;
import net.anvian.mctelemetry4j.model.Telemetry;
import net.anvian.mctelemetry4j.repository.ModRepository;
import net.anvian.mctelemetry4j.repository.TelemetryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataService {
    private final ModRepository modRepository;
    private final TelemetryRepository telemetryRepository;

    @Transactional
    public ResponseEntity<DataResponse> processData(DataRequest request) {
        McMod mcMod = modRepository.findByModId(request.mod_id()).orElseThrow(ModNotFoundException::new);

        Telemetry telemetry = telemetryRepository.findByGameVersionAndModVersionAndMod_ModIdAndLoader(
                        request.game_version(), request.mod_version(), mcMod.getModId(), request.loader())
                .orElse(null);

        if (telemetry != null) {
            telemetry = new Telemetry(telemetry, telemetry.getCount() + 1);
        } else {
            telemetry = new Telemetry(mcMod, request.game_version(), request.mod_version(), request.loader(), 1L);
        }

        telemetryRepository.save(telemetry);

        return ResponseEntity.status(201).body(new DataResponse("Data received successfully"));
    }
}
