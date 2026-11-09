package gov.santos.meioambiente.repository;

import gov.santos.meioambiente.model.Anexo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AnexoRepository extends JpaRepository<Anexo, Long> {
    Optional<Anexo> findByChamadoId(Long chamadoId);
}
