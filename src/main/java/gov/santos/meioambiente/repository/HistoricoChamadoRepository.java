package gov.santos.meioambiente.repository;

import gov.santos.meioambiente.model.HistoricoChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoChamadoRepository extends JpaRepository<HistoricoChamado, Long> {
    List<HistoricoChamado> findByChamadoIdOrderByCriadoEmAsc(Long chamadoId);
}
