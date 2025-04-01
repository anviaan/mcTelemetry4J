package net.anvian.mctelemetry4j.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "telemetry")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TelemetryId.class)
public class Telemetry {
    @Id
    @ManyToOne
    @JoinColumn(name = "mod_id")
    private McMod mod;
    @Id
    private String gameVersion;
    @Id
    private String modVersion;
    @Id
    private String loader;
    private Long count;

    public Telemetry(Telemetry telemetry, long count) {
        this.mod = telemetry.getMod();
        this.gameVersion = telemetry.getGameVersion();
        this.modVersion = telemetry.getModVersion();
        this.loader = telemetry.getLoader();
        this.count = count;
    }
}