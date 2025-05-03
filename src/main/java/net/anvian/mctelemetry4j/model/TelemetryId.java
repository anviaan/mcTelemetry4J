package net.anvian.mctelemetry4j.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TelemetryId implements Serializable {
    private McMod mod;
    private String gameVersion;
    private String modVersion;
    private String loader;
}
