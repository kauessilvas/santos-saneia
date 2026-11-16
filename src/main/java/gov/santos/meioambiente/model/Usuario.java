package gov.santos.meioambiente.model;

import gov.santos.meioambiente.enums.Perfil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, unique = true, length = 180)
    private String email;
    @Column(nullable = false, unique = true, length = 80)
    private String login;
    @Column(nullable = false)
    private String senha;
    @Column(nullable = false, length = 9)
    private String cep;
    @Column(nullable = false, length = 20)
    private String numeroResidencia;
    @Column(nullable = false)
    private LocalDate dataNascimento;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Perfil perfil;
    @Column(nullable = false)
    private boolean ativo = true;
}
