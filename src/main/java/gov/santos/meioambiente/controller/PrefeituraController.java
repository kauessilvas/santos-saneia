package gov.santos.meioambiente.controller;

import gov.santos.meioambiente.dto.ChamadoFiltroDTO;
import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.enums.TipoProblema;
import gov.santos.meioambiente.service.ChamadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/prefeitura")
public class PrefeituraController {
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
    String chamados(@ModelAttribute ChamadoFiltroDTO filtro, Model model) {
        model.addAttribute("chamados", service.filtrar(filtro));
        model.addAttribute("statuses", StatusChamado.values());
        model.addAttribute("tipos", TipoProblema.values());
        return "prefeitura/chamados";
    }

    @GetMapping("/relatorios")
    String relatorios(Model model) {
        var chamados = service.filtrar(new ChamadoFiltroDTO());
        model.addAttribute("total", chamados.size());
        model.addAttribute("statuses", StatusChamado.values());
        model.addAttribute("chamados", chamados);
        model.addAttribute("contagem", Arrays.stream(StatusChamado.values())
            .collect(Collectors.toMap(s -> s, s -> chamados.stream().filter(c -> c.getStatus() == s).count())));
        return "prefeitura/relatorios";
    }
}
