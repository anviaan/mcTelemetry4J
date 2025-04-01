package net.anvian.mctelemetry4j.repository;

import net.anvian.mctelemetry4j.model.McMod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModRepository extends JpaRepository<McMod, Long> {
    Optional<McMod> findByModId(String modId);
}
