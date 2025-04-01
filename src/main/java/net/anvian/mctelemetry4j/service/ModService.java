package net.anvian.mctelemetry4j.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.request.CreateModRequest;
import net.anvian.mctelemetry4j.dto.response.ModResponse;
import net.anvian.mctelemetry4j.exception.ModAlreadyExistsException;
import net.anvian.mctelemetry4j.model.McMod;
import net.anvian.mctelemetry4j.repository.ModRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModService {
    private final ModRepository modRepository;

    @Transactional
    public ModResponse createMod(CreateModRequest request) {
        if (modRepository.findByModId(request.mod_id()).isPresent()) {
            throw new ModAlreadyExistsException();
        }

        McMod mod = new McMod(request.mod_id(), request.mod_name());
        McMod savedMod = modRepository.save(mod);
        return new ModResponse(savedMod.getId(), savedMod.getModId(), savedMod.getModName());
    }

    public List<ModResponse> getMods() {
        List<McMod> mods = modRepository.findAll();
        return mods.stream()
                .map(mod -> new ModResponse(mod.getId(), mod.getModId(), mod.getModName()))
                .toList();
    }
}
