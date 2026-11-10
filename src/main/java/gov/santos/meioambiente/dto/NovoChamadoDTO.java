package gov.santos.meioambiente.dto;

import gov.santos.meioambiente.enums.TipoProblema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class NovoChamadoDTO {
    @NotBlank(message = "Informe o endereço da ocorrência.")
    @Size(max = 160, message = "O endereço deve ter até 160 caracteres.")
    private String endereco;

    @NotBlank(message = "Informe o bairro.")
    @Size(max = 80, message = "O bairro deve ter até 80 caracteres.")
    private String bairro;

    @NotNull(message = "Selecione o tipo de problema.")
    private TipoProblema tipo;

    @NotNull(message = "Informe a data da ocorrência.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataOcorrencia;

    @NotNull(message = "Informe a hora da ocorrência.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaOcorrencia;
}
