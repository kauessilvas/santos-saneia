package gov.santos.meioambiente.controller;

import gov.santos.meioambiente.dto.ChamadoFiltroDTO;
import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.service.ChamadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sabesp")
public class SabespController {
    private final ChamadoService service;
    public SabespController(ChamadoService service) { this.service = service; }

    @GetMapping
    String dashboard(Model model) {
        model.addAttribute("chamados", service.filtrar(new ChamadoFiltroDTO()));
        return "sabesp/dashboard";
    }

    @GetMapping("/{id}")
    String detalhes(@PathVariable Long id, Model model) {
        model.addAttribute("chamado", service.buscar(id));
        model.addAttribute("comentarios", service.comentarios(id));
        model.addAttribute("historico", service.historico(id));
        model.addAttribute("imagens", service.imagens(id));
        model.addAttribute("statuses", StatusChamado.values());
        return "sabesp/detalhes";
    }

    @PostMapping("/{id}")
    String atualizar(@PathVariable Long id, @RequestParam StatusChamado status,
                     @RequestParam(required = false) String comentario) {
        service.atualizar(id, status, comentario);
        return "redirect:/sabesp/" + id;
    }
}
