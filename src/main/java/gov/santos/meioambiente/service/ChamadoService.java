package gov.santos.meioambiente.service;

import gov.santos.meioambiente.dto.ChamadoFiltroDTO;
import gov.santos.meioambiente.enums.StatusChamado;
import gov.santos.meioambiente.enums.TipoProblema;
import gov.santos.meioambiente.model.*;
import gov.santos.meioambiente.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

@Service
public class ChamadoService {
    private final ChamadoRepository chamados;
    private final AnexoRepository anexos;
    private final ComentarioRepository comentarios;
    private final HistoricoChamadoRepository historico;

    public ChamadoService(ChamadoRepository chamados, AnexoRepository anexos,
                          ComentarioRepository comentarios, HistoricoChamadoRepository historico) {
        this.chamados = chamados;
        this.anexos = anexos;
        this.comentarios = comentarios;
        this.historico = historico;
    }

    @Transactional(rollbackFor = IOException.class)
    public Chamado criar(String endereco, String bairro, TipoProblema tipo, LocalDate data,
                         LocalTime hora, MultipartFile[] imagens) throws IOException {
        if (endereco == null || endereco.isBlank() || bairro == null || bairro.isBlank()
                || endereco.length() > 160 || bairro.length() > 80 || tipo == null || data == null
                || hora == null || data.isBefore(LocalDate.now().minusYears(5))
                || data.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Preencha endereço, bairro, tipo, data e hora válidos. A ocorrência deve ser dos últimos 5 anos.");
        }
        List<MultipartFile> arquivos = imagens == null ? List.of() : Arrays.stream(imagens)
            .filter(arquivo -> arquivo != null && !arquivo.isEmpty()).toList();
        if (arquivos.size() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie no máximo 5 imagens.");
        }
        Set<String> tiposPermitidos = Set.of("image/jpeg", "image/png", "image/webp");
        for (MultipartFile arquivo : arquivos) {
            if (arquivo.getSize() > 5_000_000 || arquivo.getContentType() == null
                    || !tiposPermitidos.contains(arquivo.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cada imagem deve ser JPEG, PNG ou WebP e ter até 5 MB.");
            }
        }
        Chamado chamado = new Chamado();
        chamado.setEndereco(endereco.trim());
        chamado.setBairro(bairro.trim());
        chamado.setTipo(tipo);
        chamado.setDataOcorrencia(data);
        chamado.setHoraOcorrencia(hora);
        chamados.save(chamado);
        for (MultipartFile imagem : arquivos) {
            Anexo anexo = new Anexo();
            anexo.setChamado(chamado);
            anexo.setTipoConteudo(imagem.getContentType());
            anexo.setDados(imagem.getBytes());
            anexos.save(anexo);
        }
        registrarHistorico(chamado);
        return chamado;
    }

    @Transactional(readOnly = true)
    public Chamado buscar(Long id) {
        return chamados.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamado não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Chamado> filtrar(ChamadoFiltroDTO filtro) {
        Specification<Chamado> spec = (root, query, cb) -> cb.conjunction();
        if (filtro.getStatus() != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("status"), filtro.getStatus()));
        if (filtro.getTipo() != null) spec = spec.and((r, q, cb) -> cb.equal(r.get("tipo"), filtro.getTipo()));
        if (filtro.getBairro() != null && !filtro.getBairro().isBlank()) {
            String bairro = "%" + filtro.getBairro().trim().toLowerCase() + "%";
            spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("bairro")), bairro));
        }
        if (filtro.getInicio() != null) spec = spec.and((r, q, cb) -> cb.greaterThanOrEqualTo(r.get("dataOcorrencia"), filtro.getInicio()));
        if (filtro.getFim() != null) spec = spec.and((r, q, cb) -> cb.lessThanOrEqualTo(r.get("dataOcorrencia"), filtro.getFim()));
        return chamados.findAll(spec, Sort.by(Sort.Direction.DESC, "criadoEm"));
    }

    @Transactional
    public void atualizar(Long id, StatusChamado status, String comentario) {
        Chamado chamado = buscar(id);
        if (status == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um status.");
        if (comentario != null && comentario.length() > 1000) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comentário deve ter até 1000 caracteres.");
        if (chamado.getStatus() != status) {
            chamado.setStatus(status);
            registrarHistorico(chamado);
        }
        if (comentario != null && !comentario.isBlank()) {
            Comentario item = new Comentario();
            item.setChamado(chamado);
            item.setTexto(comentario.trim());
            comentarios.save(item);
        }
    }

    private void registrarHistorico(Chamado chamado) {
        HistoricoChamado item = new HistoricoChamado();
        item.setChamado(chamado);
        item.setStatus(chamado.getStatus());
        historico.save(item);
    }

    public List<Comentario> comentarios(Long id) { buscar(id); return comentarios.findByChamadoIdOrderByCriadoEmAsc(id); }
    public List<HistoricoChamado> historico(Long id) { buscar(id); return historico.findByChamadoIdOrderByCriadoEmAsc(id); }
    public List<Long> imagens(Long id) { buscar(id); return anexos.findIdsByChamadoId(id); }
    public Anexo imagem(Long chamadoId, Long imagemId) {
        buscar(chamadoId);
        return anexos.findByIdAndChamadoId(imagemId, chamadoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não encontrada."));
    }
    public Anexo primeiraImagem(Long chamadoId) {
        buscar(chamadoId);
        return anexos.findFirstByChamadoIdOrderByIdAsc(chamadoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não encontrada."));
    }
}
