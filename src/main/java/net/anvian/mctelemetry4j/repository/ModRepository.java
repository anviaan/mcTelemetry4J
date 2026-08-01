package net.anvian.mctelemetry4j.repository;

import net.anvian.mctelemetry4j.model.McMod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModRepository extends JpaRepository<McMod, Long> {
    Optional<McMod> findByModId(String modId);
}
