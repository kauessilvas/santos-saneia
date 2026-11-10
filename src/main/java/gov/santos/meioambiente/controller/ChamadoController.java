package gov.santos.meioambiente.controller;

import gov.santos.meioambiente.enums.TipoProblema;
import gov.santos.meioambiente.service.ChamadoService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ChamadoController {
    private final ChamadoService service;
    public ChamadoController(ChamadoService service) { this.service = service; }

    @GetMapping("/cidadao/cadastro")
    String cadastro(Model model) {
        model.addAttribute("tipos", TipoProblema.values());
        model.addAttribute("dataMinima", LocalDate.now().minusYears(5));
        model.addAttribute("dataMaxima", LocalDate.now());
        return "cidadao/cadastro";
    }

    @PostMapping("/cidadao/chamados")
    String criar(@RequestParam String endereco, @RequestParam String bairro, @RequestParam TipoProblema tipo,
                 @RequestParam LocalDate dataOcorrencia, @RequestParam LocalTime horaOcorrencia,
                 @RequestParam(required = false) MultipartFile[] imagens) throws IOException {
        return "redirect:/cidadao/consulta/" + service.criar(endereco, bairro, tipo, dataOcorrencia, horaOcorrencia, imagens).getId();
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
