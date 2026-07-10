package net.anvian.mctelemetry4j.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.config.OpenApiConfig;
import net.anvian.mctelemetry4j.dto.request.CreateModRequest;
import net.anvian.mctelemetry4j.dto.response.ModResponse;
import net.anvian.mctelemetry4j.service.ModService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/mods", "/telemetry/mods"})
@RequiredArgsConstructor
@Tag(name = "Mods", description = "Administrator-only registration and maintenance of reporting mods.")
public class ModController {
    private final ModService modService;

    @PostMapping
    @Operation(summary = "Register a mod", security = @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME))
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Mod created", content = @Content(schema = @Schema(implementation = ModResponse.class))), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "401", description = "Authentication required"), @ApiResponse(responseCode = "409", description = "Mod already exists")})
    public ResponseEntity<ModResponse> createMod(@Valid @RequestBody CreateModRequest request) {
        ModResponse createdMod = modService.createMod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMod);
    }

    @GetMapping
    @Operation(summary = "List registered mods", description = "Available publicly so telemetry clients can discover registered mods.")
    @ApiResponse(responseCode = "200", description = "Registered mods", content = @Content(schema = @Schema(implementation = ModResponse.class)))
    public ResponseEntity<List<ModResponse>> getAllMods() {
        return ResponseEntity.ok(modService.getMods());
    }

    @DeleteMapping
    @Operation(summary = "Delete a mod", security = @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME))
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Mod deleted"), @ApiResponse(responseCode = "401", description = "Authentication required"), @ApiResponse(responseCode = "404", description = "Mod not found")})
    public ResponseEntity<Void> deleteMod(@RequestParam long id) {
        modService.deleteMod(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Update a mod", security = @SecurityRequirement(name = OpenApiConfig.BASIC_AUTH_SCHEME))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Mod updated", content = @Content(schema = @Schema(implementation = ModResponse.class))), @ApiResponse(responseCode = "400", description = "Invalid request"), @ApiResponse(responseCode = "401", description = "Authentication required"), @ApiResponse(responseCode = "404", description = "Mod not found"), @ApiResponse(responseCode = "409", description = "Mod already exists")})
    public ResponseEntity<ModResponse> updateMod(@RequestParam long id, @Valid @RequestBody CreateModRequest request) {
        return ResponseEntity.ok(modService.updateMod(id, request));
    }
}
