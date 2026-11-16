package gov.santos.meioambiente.repository;

import gov.santos.meioambiente.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByLogin(String login);
}
