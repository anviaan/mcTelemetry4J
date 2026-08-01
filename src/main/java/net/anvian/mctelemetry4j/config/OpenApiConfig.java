package net.anvian.mctelemetry4j.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BASIC_AUTH_SCHEME = "basicAuth";

    @Bean
    public OpenAPI mcTelemetryOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("McTelemetry4J API")
                        .version("1.2.1")
                        .description("API for collecting anonymous, aggregated Minecraft mod telemetry and exporting its results."))
                .components(new Components().addSecuritySchemes(BASIC_AUTH_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Administrator credentials required for mod management and telemetry exports.")));
    }

    @Bean
    public OpenApiCustomizer markCompatibilityRoutesDeprecated() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            if (path.startsWith("/telemetry/")) {
                pathItem.readOperations().forEach(operation -> operation.setDeprecated(true));
            }
        });
    }
}
