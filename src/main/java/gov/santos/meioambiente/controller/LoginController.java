package gov.santos.meioambiente.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/") String inicio() { return "redirect:/cidadao/cadastro"; }
    @GetMapping("/login") String login() { return "login"; }
    @GetMapping("/painel") String painel(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SABESP"))) return "redirect:/sabesp";
        return "redirect:/prefeitura";
    }
}
