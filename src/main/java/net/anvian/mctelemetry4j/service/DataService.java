package net.anvian.mctelemetry4j.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.request.DataRequest;
import net.anvian.mctelemetry4j.dto.response.DataResponse;
import net.anvian.mctelemetry4j.exception.ModNotFoundException;
import net.anvian.mctelemetry4j.model.McMod;
import net.anvian.mctelemetry4j.repository.ModRepository;
import net.anvian.mctelemetry4j.repository.TelemetryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class DataService {
    private final ModRepository modRepository;
    private final TelemetryRepository telemetryRepository;

    @Transactional
    public ResponseEntity<DataResponse> processData(DataRequest request) {
        McMod mcMod = modRepository.findByModId(request.mod_id()).orElseThrow(ModNotFoundException::new);

        String period = YearMonth.now(ZoneOffset.UTC).toString();
        telemetryRepository.upsertTelemetry(mcMod.getId(), period, request.game_version(), request.mod_version(), request.loader());

        return ResponseEntity.status(201).body(new DataResponse("Data received successfully"));
    }

}
