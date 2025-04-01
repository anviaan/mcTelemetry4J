package net.anvian.mctelemetry4j.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mc_mod")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class McMod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String modId;
    private String modName;
    @OneToMany(mappedBy = "mod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telemetry> telemetry = new ArrayList<>();

    public McMod(@NotBlank String modId, @NotBlank String modName) {
        this.modId = modId;
        this.modName = modName;
    }
}
