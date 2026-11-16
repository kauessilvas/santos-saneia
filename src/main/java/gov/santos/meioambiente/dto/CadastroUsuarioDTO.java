package gov.santos.meioambiente.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter @Setter
public class CadastroUsuarioDTO {
    @NotBlank(message = "Informe seu nome completo.")
    @Size(max = 120, message = "O nome deve ter até 120 caracteres.")
    private String nome;
    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 180, message = "O e-mail deve ter até 180 caracteres.")
    private String email;
    @NotNull(message = "Informe sua data de nascimento.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "Informe um CEP válido.")
    private String cep;
    @Size(max = 20, message = "O número deve ter até 20 caracteres.")
    private String numeroResidencia;
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    private String senha;
    private String confirmarSenha;
}
