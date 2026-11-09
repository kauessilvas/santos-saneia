package gov.santos.meioambiente.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Anexo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Chamado chamado;
    @Column(nullable = false)
    private String tipoConteudo;
    @Lob @Column(nullable = false)
    private byte[] dados;
}
