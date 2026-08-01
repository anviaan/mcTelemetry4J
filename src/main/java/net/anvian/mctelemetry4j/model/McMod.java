package net.anvian.mctelemetry4j.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mc_mod")
@Getter
@Setter
@NoArgsConstructor
public class McMod {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mc_mod_seq")
    @SequenceGenerator(name = "mc_mod_seq", sequenceName = "mc_mod_seq", allocationSize = 50)
    private Long id;
    @Column(unique = true)
    private String modId;
    private String modName;

    public McMod(@NotBlank String modId, @NotBlank String modName) {
        this.modId = modId;
        this.modName = modName;
    }
}
