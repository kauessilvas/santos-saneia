package gov.santos.meioambiente.model;

import gov.santos.meioambiente.enums.StatusChamado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class HistoricoChamado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Chamado chamado;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private StatusChamado status;
    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
