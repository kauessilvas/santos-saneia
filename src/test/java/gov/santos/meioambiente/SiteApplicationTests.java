package gov.santos.meioambiente;

import gov.santos.meioambiente.dto.ChamadoFiltroDTO;
import gov.santos.meioambiente.dto.CadastroUsuarioDTO;
import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.enums.TipoProblema;
import gov.santos.meioambiente.service.ChamadoService;
import gov.santos.meioambiente.service.UsuarioService;
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
    @Autowired UsuarioService usuarios;

    @Test
    void registraAtualizaEFiltraChamado() throws Exception {
        byte[] imagem = new byte[] {1, 2, 3};
        var arquivo = new MockMultipartFile("imagens", "foto.png", "image/png", imagem);
        var segundaImagem = new MockMultipartFile("imagens", "foto2.jpg", "image/jpeg", new byte[] {4, 5});
        var cadastro = new CadastroUsuarioDTO();
        cadastro.setNome("Maria Teste"); cadastro.setEmail("maria.teste@example.com");
        cadastro.setDataNascimento(LocalDate.now().minusYears(30)); cadastro.setCep("11000-000");
        cadastro.setNumeroResidencia("10"); cadastro.setSenha("senha1234");
        var usuario = usuarios.cadastrarCidadao(cadastro);
        var chamado = service.criar("Rua Teste, 10", "Centro", TipoProblema.VAZAMENTO,
            LocalDate.now(), LocalTime.of(12, 30), new MockMultipartFile[] {arquivo, segundaImagem}, usuario);
        assertEquals(StatusChamado.ABERTO, chamado.getStatus());
        var idsImagens = service.imagens(chamado.getId());
        assertEquals(2, idsImagens.size());
        assertArrayEquals(imagem, service.imagem(chamado.getId(), idsImagens.getFirst()).getDados());
        assertArrayEquals(new byte[] {4, 5}, service.imagem(chamado.getId(), idsImagens.get(1)).getDados());

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
            TipoProblema.VAZAMENTO, LocalDate.now().minusYears(6), LocalTime.NOON, null, usuario));
        assertThrows(ResponseStatusException.class, () -> service.criar("Rua Teste, 10", "Centro",
            TipoProblema.VAZAMENTO, LocalDate.now(), LocalTime.NOON,
            new MockMultipartFile[] {arquivo, arquivo, arquivo, arquivo, arquivo, arquivo}, usuario));
    }
}
