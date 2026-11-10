package gov.santos.meioambiente.controller;

import gov.santos.meioambiente.dto.ChamadoFiltroDTO;
import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.enums.TipoProblema;
import gov.santos.meioambiente.service.ChamadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import java.util.Arrays;

@Controller
@RequestMapping("/prefeitura")
public class PrefeituraController {
    public record ResumoStatus(StatusChamado status, long total) {}
    private final ChamadoService service;
    public PrefeituraController(ChamadoService service) { this.service = service; }

    @GetMapping
    String dashboard(Model model) {
        var chamados = service.filtrar(new ChamadoFiltroDTO());
        model.addAttribute("total", chamados.size());
        model.addAttribute("abertos", chamados.stream().filter(c -> c.getStatus() == StatusChamado.ABERTO).count());
        model.addAttribute("resolvidos", chamados.stream().filter(c -> c.getStatus() == StatusChamado.RESOLVIDO).count());
        model.addAttribute("chamados", chamados);
        return "prefeitura/dashboard";
    }

    @GetMapping("/chamados")
    String chamados(@ModelAttribute ChamadoFiltroDTO filtro, BindingResult validacao, Model model) {
        if (validacao.hasErrors()) {
            model.addAttribute("erroFiltro", "Informe datas válidas para filtrar os chamados.");
            model.addAttribute("chamados", java.util.List.of());
        } else if (filtro.getInicio() != null && filtro.getFim() != null && filtro.getInicio().isAfter(filtro.getFim())) {
            model.addAttribute("erroFiltro", "A data inicial deve ser anterior ou igual à data final.");
            model.addAttribute("chamados", java.util.List.of());
        } else {
            model.addAttribute("chamados", service.filtrar(filtro));
        }
        model.addAttribute("statuses", StatusChamado.values());
        model.addAttribute("tipos", TipoProblema.values());
        return "prefeitura/chamados";
    }

    @GetMapping("/relatorios")
    String relatorios(Model model) {
        var chamados = service.filtrar(new ChamadoFiltroDTO());
        model.addAttribute("total", chamados.size());
        model.addAttribute("resumos", Arrays.stream(StatusChamado.values())
            .map(s -> new ResumoStatus(s, chamados.stream().filter(c -> c.getStatus() == s).count()))
            .toList());
        return "prefeitura/relatorios";
    }
}
