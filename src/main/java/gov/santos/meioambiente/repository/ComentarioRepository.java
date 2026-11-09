package gov.santos.meioambiente.repository;

import gov.santos.meioambiente.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByChamadoIdOrderByCriadoEmAsc(Long chamadoId);
}
