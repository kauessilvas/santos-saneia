package gov.santos.meioambiente.dto;

import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.enums.TipoProblema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter @Setter
public class ChamadoFiltroDTO {
    private StatusChamado status;
    private TipoProblema tipo;
    private String bairro;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate inicio;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fim;
}
