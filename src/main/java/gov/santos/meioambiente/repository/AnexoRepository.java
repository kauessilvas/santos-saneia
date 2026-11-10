package gov.santos.meioambiente.repository;

import gov.santos.meioambiente.model.Anexo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AnexoRepository extends JpaRepository<Anexo, Long> {
    @Query("select a.id from Anexo a where a.chamado.id = :chamadoId order by a.id")
    List<Long> findIdsByChamadoId(@Param("chamadoId") Long chamadoId);
    Optional<Anexo> findByIdAndChamadoId(Long id, Long chamadoId);
    Optional<Anexo> findFirstByChamadoIdOrderByIdAsc(Long chamadoId);
}
