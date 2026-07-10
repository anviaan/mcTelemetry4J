package net.anvian.mctelemetry4j.repository;

import net.anvian.mctelemetry4j.dto.response.ModPeriodStatsResponse;
import net.anvian.mctelemetry4j.model.Telemetry;
import net.anvian.mctelemetry4j.model.TelemetryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface TelemetryRepository extends JpaRepository<Telemetry, TelemetryId> {
    @Modifying
    @Query(value = """
                INSERT INTO telemetry (mod_id, period, game_version, mod_version, loader, count)
                VALUES (:modId, :period, :gameVersion, :modVersion, :loader, 1)
                ON CONFLICT (mod_id, period, game_version, mod_version, loader)
                DO UPDATE SET count = telemetry.count + 1
            """, nativeQuery = true)
    void upsertTelemetry(Long modId, String period, String gameVersion, String modVersion, String loader);

    @Query("SELECT t FROM Telemetry t JOIN FETCH t.mod WHERE (:period IS NULL OR t.period = :period)")
    Stream<Telemetry> streamAll(@Param("period") String period);

    @Query("""
                SELECT new net.anvian.mctelemetry4j.dto.response.ModPeriodStatsResponse(
                    t.mod.modId, t.mod.modName, t.period, SUM(t.count))
                FROM Telemetry t
                WHERE (:period IS NULL OR t.period = :period)
                GROUP BY t.mod.modId, t.mod.modName, t.period
                ORDER BY t.period DESC, t.mod.modId
            """)
    List<ModPeriodStatsResponse> aggregatedStats(@Param("period") String period);

    @Query("""
                SELECT DISTINCT t.period
                FROM Telemetry t
                ORDER BY t.period DESC
            """)
    List<String> findAvailablePeriods();
}
