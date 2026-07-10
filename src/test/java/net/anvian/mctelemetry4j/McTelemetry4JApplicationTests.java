package net.anvian.mctelemetry4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class McTelemetry4JApplicationTests {

    @Test
    void applicationClassLoadsWithoutExternalDatabase() {
        assertNotNull(McTelemetry4JApplication.class);
    }

}
