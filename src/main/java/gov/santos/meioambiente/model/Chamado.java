package gov.santos.meioambiente.model;

import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.enums.TipoProblema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter @Setter
public class Chamado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String endereco;
    @Column(nullable = false, length = 80)
    private String bairro;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TipoProblema tipo;
    @Column(nullable = false)
    private LocalDate dataOcorrencia;
    @Column(nullable = false)
    private LocalTime horaOcorrencia;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private StatusChamado status = StatusChamado.ABERTO;
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
