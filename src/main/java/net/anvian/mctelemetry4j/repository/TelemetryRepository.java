package net.anvian.mctelemetry4j.repository;

import net.anvian.mctelemetry4j.model.Telemetry;
import net.anvian.mctelemetry4j.model.TelemetryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

public interface TelemetryRepository extends JpaRepository<Telemetry, TelemetryId> {
    Optional<Telemetry> findByGameVersionAndModVersionAndMod_ModIdAndLoader(String gameVersion, String modVersion, String modId, String loader);

    @Modifying
    @Query(value = """
        INSERT INTO telemetry (mod_id, game_version, mod_version, loader, count)
        VALUES (:modId, :gameVersion, :modVersion, :loader, 1)
        ON CONFLICT (mod_id, game_version, mod_version, loader)
        DO UPDATE SET count = telemetry.count + 1
    """, nativeQuery = true)
    void upsertTelemetry(Long modId, String gameVersion, String modVersion, String loader);

    @Query("SELECT t FROM Telemetry t JOIN FETCH t.mod")
    Stream<Telemetry> streamAll();
}
