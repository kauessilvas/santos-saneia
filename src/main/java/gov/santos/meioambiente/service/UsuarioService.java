package gov.santos.meioambiente.service;

import gov.santos.meioambiente.dto.CadastroUsuarioDTO;
import gov.santos.meioambiente.enums.Perfil;
import gov.santos.meioambiente.model.Usuario;
import gov.santos.meioambiente.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        Usuario usuario = usuarios.findByLogin(login.toLowerCase())
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        return User.withUsername(usuario.getLogin()).password(usuario.getSenha())
            .roles(usuario.getPerfil().name()).disabled(!usuario.isAtivo()).build();
    }

    @Transactional
    public Usuario cadastrarCidadao(CadastroUsuarioDTO dados) {
        if (dados.getDataNascimento() == null || dados.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "É necessário ter pelo menos 18 anos para abrir chamados.");
        }
        String email = dados.getEmail().trim().toLowerCase();
        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já possui cadastro.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dados.getNome().trim());
        usuario.setEmail(email);
        usuario.setLogin(email);
        usuario.setSenha(encoder.encode(dados.getSenha()));
        usuario.setCep(dados.getCep().replaceAll("[^0-9]", ""));
        usuario.setNumeroResidencia(dados.getNumeroResidencia().trim());
        usuario.setDataNascimento(dados.getDataNascimento());
        usuario.setPerfil(Perfil.CIDADAO);
        return usuarios.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorLogin(String login) {
        return usuarios.findByLogin(login.toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    @Transactional
    public void criarAdministrador(String login, String email, String senha, Perfil perfil) {
        if (usuarios.existsByLogin(login)) return;
        Usuario usuario = new Usuario();
        usuario.setNome(perfil == Perfil.SABESP ? "Atendimento Sabesp" : "Administração Prefeitura");
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setSenha(encoder.encode(senha));
        usuario.setCep("00000000");
        usuario.setNumeroResidencia("S/N");
        usuario.setDataNascimento(LocalDate.of(1970, 1, 1));
        usuario.setPerfil(perfil);
        usuarios.save(usuario);
    }
}
