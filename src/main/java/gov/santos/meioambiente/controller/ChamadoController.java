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
                 @RequestParam(required = false) MultipartFile imagem) throws IOException {
        return "redirect:/cidadao/consulta/" + service.criar(endereco, bairro, tipo, dataOcorrencia, horaOcorrencia, imagem).getId();
    }

    @GetMapping("/cidadao/consulta/{id}")
    String consulta(@PathVariable Long id, Model model) {
        model.addAttribute("chamado", service.buscar(id));
        model.addAttribute("comentarios", service.comentarios(id));
        model.addAttribute("historico", service.historico(id));
        model.addAttribute("temImagem", service.temImagem(id));
        return "cidadao/consulta";
    }

    @GetMapping("/chamados/{id}/imagem")
    ResponseEntity<byte[]> imagem(@PathVariable Long id) {
        var anexo = service.imagem(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(anexo.getTipoConteudo()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline").body(anexo.getDados());
    }
}
