package gov.santos.meioambiente.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Comentario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Chamado chamado;
    @Column(nullable = false, length = 1000)
    private String texto;
    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}
