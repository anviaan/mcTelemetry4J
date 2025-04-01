package net.anvian.mctelemetry4j.repository;

import net.anvian.mctelemetry4j.model.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    Optional<Telemetry> findByGameVersionAndModVersionAndMod_ModIdAndLoader(String gameVersion, String modVersion, String modId, String loader);
}
