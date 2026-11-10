package gov.santos.meioambiente.controller;

import gov.santos.meioambiente.enums.TipoProblema;
import gov.santos.meioambiente.dto.NovoChamadoDTO;
import gov.santos.meioambiente.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.time.LocalDate;

@Controller
public class ChamadoController {
    private final ChamadoService service;
    public ChamadoController(ChamadoService service) { this.service = service; }

    @GetMapping("/cidadao/cadastro")
    String cadastro(Model model) {
        model.addAttribute("formulario", new NovoChamadoDTO());
        return prepararCadastro(model);
    }

    private String prepararCadastro(Model model) {
        model.addAttribute("tipos", TipoProblema.values());
        model.addAttribute("dataMinima", LocalDate.now().minusYears(5));
        model.addAttribute("dataMaxima", LocalDate.now());
        return "cidadao/cadastro";
    }

    @PostMapping("/cidadao/chamados")
    String criar(@Valid @ModelAttribute("formulario") NovoChamadoDTO formulario, BindingResult validacao,
                 @RequestParam(required = false) MultipartFile[] imagens, Model model) throws IOException {
        if (validacao.hasErrors()) return prepararCadastro(model);
        try {
            var chamado = service.criar(formulario.getEndereco(), formulario.getBairro(), formulario.getTipo(),
                formulario.getDataOcorrencia(), formulario.getHoraOcorrencia(), imagens);
            return "redirect:/cidadao/consulta/" + chamado.getId() + "?criado=1";
        } catch (ResponseStatusException erro) {
            model.addAttribute("erro", erro.getReason());
            return prepararCadastro(model);
        }
    }

    @GetMapping("/cidadao/consulta/{id}")
    String consulta(@PathVariable Long id, Model model) {
        model.addAttribute("chamado", service.buscar(id));
        model.addAttribute("comentarios", service.comentarios(id));
        model.addAttribute("historico", service.historico(id));
        model.addAttribute("imagens", service.imagens(id));
        return "cidadao/consulta";
    }

    @GetMapping("/chamados/{id}/imagens/{imagemId}")
    ResponseEntity<byte[]> imagem(@PathVariable Long id, @PathVariable Long imagemId) {
        return respostaImagem(service.imagem(id, imagemId));
    }

    // Mantém links antigos para a primeira imagem do chamado.
    @GetMapping("/chamados/{id}/imagem")
    ResponseEntity<byte[]> primeiraImagem(@PathVariable Long id) {
        return respostaImagem(service.primeiraImagem(id));
    }

    private ResponseEntity<byte[]> respostaImagem(gov.santos.meioambiente.model.Anexo anexo) {
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(anexo.getTipoConteudo()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline").body(anexo.getDados());
    }
}
