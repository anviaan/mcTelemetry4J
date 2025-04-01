package net.anvian.mctelemetry4j.model;

import lombok.Setter;

import java.io.Serializable;

@Setter
public class TelemetryId implements Serializable {
    private Long mod;
    private String gameVersion;
    private String modVersion;
    private String loader;
}
