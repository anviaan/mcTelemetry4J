package net.anvian.mctelemetry4j.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.request.CreateModRequest;
import net.anvian.mctelemetry4j.dto.response.ModResponse;
import net.anvian.mctelemetry4j.service.ModService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mods")
@RequiredArgsConstructor
public class ModController {
    private final ModService modService;

    @PostMapping
    public ResponseEntity<ModResponse> createMod(@Valid @RequestBody CreateModRequest request) {
        ModResponse createdMod = modService.createMod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMod);
    }

    @GetMapping
    public ResponseEntity<List<ModResponse>> getMod() {
        List<ModResponse> mods = modService.getMods();
        return ResponseEntity.ok(mods);
    }
}
