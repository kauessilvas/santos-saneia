package gov.santos.meioambiente.controller;

import gov.santos.meioambiente.dto.CadastroUsuarioDTO;
import gov.santos.meioambiente.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;

@Controller
@SessionAttributes("cadastro")
public class CadastroController {
    private final UsuarioService usuarios;
    public CadastroController(UsuarioService usuarios) { this.usuarios = usuarios; }

    @ModelAttribute("cadastro")
    CadastroUsuarioDTO formulario() { return new CadastroUsuarioDTO(); }

    @GetMapping("/cadastro")
    String etapaUm(Model model) {
        model.addAttribute("etapa", 1);
        return "cadastro/etapa-1";
    }

    @PostMapping("/cadastro/etapa-1")
    String salvarEtapaUm(@Valid @ModelAttribute("cadastro") CadastroUsuarioDTO cadastro,
                         BindingResult erros, Model model) {
        if (cadastro.getDataNascimento() != null && cadastro.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            erros.rejectValue("dataNascimento", "idade", "É necessário ter pelo menos 18 anos.");
        }
        if (erros.hasErrors()) { model.addAttribute("etapa", 1); return "cadastro/etapa-1"; }
        return "redirect:/cadastro/etapa-2";
    }

    @GetMapping("/cadastro/etapa-2")
    String etapaDois(@ModelAttribute("cadastro") CadastroUsuarioDTO cadastro, Model model) {
        model.addAttribute("etapa", 2);
        return "cadastro/etapa-2";
    }

    @PostMapping("/cadastro/etapa-2")
    String salvarEtapaDois(@ModelAttribute("cadastro") CadastroUsuarioDTO cadastro,
                           BindingResult erros, Model model, SessionStatus status) {
        if (cadastro.getCep() == null || !cadastro.getCep().matches("\\d{5}-?\\d{3}"))
            erros.rejectValue("cep", "cep", "Informe um CEP válido.");
        if (cadastro.getNumeroResidencia() == null || cadastro.getNumeroResidencia().isBlank())
            erros.rejectValue("numeroResidencia", "numero", "Informe o número da residência.");
        if (cadastro.getSenha() == null || cadastro.getSenha().length() < 8)
            erros.rejectValue("senha", "senha", "A senha deve ter pelo menos 8 caracteres.");
        if (cadastro.getSenha() != null && !cadastro.getSenha().equals(cadastro.getConfirmarSenha()))
            erros.rejectValue("confirmarSenha", "confirmacao", "As senhas não conferem.");
        if (erros.hasErrors()) { model.addAttribute("etapa", 2); return "cadastro/etapa-2"; }
        try {
            usuarios.cadastrarCidadao(cadastro);
            status.setComplete();
            return "redirect:/login?cadastrado=1";
        } catch (ResponseStatusException erro) {
            model.addAttribute("erro", erro.getReason());
            model.addAttribute("etapa", 2);
            return "cadastro/etapa-2";
        }
    }
}
