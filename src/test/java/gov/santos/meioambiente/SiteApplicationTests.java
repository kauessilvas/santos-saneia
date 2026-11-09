package gov.santos.meioambiente;

import gov.santos.meioambiente.dto.ChamadoFiltroDTO;
import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.enums.TipoProblema;
import gov.santos.meioambiente.service.ChamadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class SiteApplicationTests {
    @Autowired ChamadoService service;

    @Test
    void registraAtualizaEFiltraChamado() throws Exception {
        byte[] imagem = new byte[] {1, 2, 3};
        var arquivo = new MockMultipartFile("imagem", "foto.png", "image/png", imagem);
        var chamado = service.criar("Rua Teste, 10", "Centro", TipoProblema.VAZAMENTO,
            LocalDate.now(), LocalTime.of(12, 30), arquivo);
        assertEquals(StatusChamado.ABERTO, chamado.getStatus());
        assertArrayEquals(imagem, service.imagem(chamado.getId()).getDados());

        service.atualizar(chamado.getId(), StatusChamado.EM_ATENDIMENTO, "Equipe acionada");
        assertEquals(StatusChamado.EM_ATENDIMENTO, service.buscar(chamado.getId()).getStatus());
        assertEquals("Equipe acionada", service.comentarios(chamado.getId()).getFirst().getTexto());
        assertEquals(2, service.historico(chamado.getId()).size());

        var filtro = new ChamadoFiltroDTO();
        filtro.setStatus(StatusChamado.EM_ATENDIMENTO);
        filtro.setBairro("centro");
        assertTrue(service.filtrar(filtro).stream().anyMatch(c -> c.getId().equals(chamado.getId())));
        filtro.setStatus(StatusChamado.RESOLVIDO);
        assertTrue(service.filtrar(filtro).isEmpty());

        assertThrows(ResponseStatusException.class, () -> service.criar("Rua Teste, 10", "Centro",
            TipoProblema.VAZAMENTO, LocalDate.now().minusYears(6), LocalTime.NOON, null));
    }
}
