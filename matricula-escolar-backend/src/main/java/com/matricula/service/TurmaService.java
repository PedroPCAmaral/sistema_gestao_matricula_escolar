package com.matricula.service;

import com.matricula.dto.TurmaDTO;
import com.matricula.model.Turma;
import com.matricula.model.Usuario;
import com.matricula.repository.TurmaRepository;
import com.matricula.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço: TurmaService
 * Responsável pela lógica de negócio de turmas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Lista todas as turmas ativas
     */
    public List<TurmaDTO> listarTodas() {
        log.info("Listando todas as turmas");
        return turmaRepository.findByAtivoTrueOrderBySerieAscTurnoAsc()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma turma por ID
     */
    public TurmaDTO buscarPorId(Integer id) {
        log.info("Buscando turma com ID: {}", id);
        return turmaRepository.findById(id)
                .map(this::converterParaDTO)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
    }

    /**
     * Cria uma nova turma
     */
    @Transactional
    public TurmaDTO criar(TurmaDTO turmaDTO) {
        log.info("Criando nova turma: {}", turmaDTO.getNome());

        Turma turma = new Turma();
        turma.setNome(turmaDTO.getNome());
        turma.setSerie(turmaDTO.getSerie());
        turma.setTurno(Turma.Turno.valueOf(turmaDTO.getTurno()));
        turma.setCapacidade(turmaDTO.getCapacidade());
        turma.setAtivo(true);

        // Associar professor se fornecido
        if (turmaDTO.getProfessorId() != null) {
            Usuario professor = usuarioRepository.findById(turmaDTO.getProfessorId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            turma.setProfessor(professor);
        }

        Turma turmaSalva = turmaRepository.save(turma);
        log.info("Turma criada com sucesso: ID {}", turmaSalva.getId());

        return converterParaDTO(turmaSalva);
    }

    /**
     * Atualiza uma turma existente
     */
    @Transactional
    public TurmaDTO atualizar(Integer id, TurmaDTO turmaDTO) {
        log.info("Atualizando turma com ID: {}", id);

        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        turma.setNome(turmaDTO.getNome());
        turma.setSerie(turmaDTO.getSerie());
        turma.setTurno(Turma.Turno.valueOf(turmaDTO.getTurno()));
        turma.setCapacidade(turmaDTO.getCapacidade());

        if (turmaDTO.getProfessorId() != null) {
            Usuario professor = usuarioRepository.findById(turmaDTO.getProfessorId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            turma.setProfessor(professor);
        }

        Turma turmaAtualizada = turmaRepository.save(turma);
        log.info("Turma atualizada com sucesso: ID {}", id);

        return converterParaDTO(turmaAtualizada);
    }

    /**
     * Deleta uma turma (soft delete - marca como inativa)
     */
    @Transactional
    public void deletar(Integer id) {
        log.info("Deletando turma com ID: {}", id);

        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        turma.setAtivo(false);
        turmaRepository.save(turma);

        log.info("Turma deletada com sucesso: ID {}", id);
    }

    /**
     * Converte uma Turma para TurmaDTO
     */
    private TurmaDTO converterParaDTO(Turma turma) {
        return TurmaDTO.builder()
                .id(turma.getId())
                .nome(turma.getNome())
                .serie(turma.getSerie())
                .turno(turma.getTurno().toString())
                .capacidade(turma.getCapacidade())
                .professorId(turma.getProfessor() != null ? turma.getProfessor().getId() : null)
                .professorNome(turma.getProfessor() != null ? turma.getProfessor().getNome() : null)
                .ativo(turma.getAtivo())
                .dataCriacao(turma.getDataCriacao())
                .dataAtualizacao(turma.getDataAtualizacao())
                .build();
    }
}
