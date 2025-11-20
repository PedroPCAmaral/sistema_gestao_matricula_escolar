package com.matricula.service;

import com.matricula.dto.MatriculaDTO;
import com.matricula.model.Aluno;
import com.matricula.model.Matricula;
import com.matricula.model.Turma;
import com.matricula.repository.AlunoRepository;
import com.matricula.repository.MatriculaRepository;
import com.matricula.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço: MatriculaService
 * Responsável pela lógica de negócio de matrículas
 * Integra com Redis para fila de requisições de matrícula
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FILA_MATRICULAS_KEY = "fila:matriculas";
    private static final String FILA_CANCELAMENTOS_KEY = "fila:cancelamentos";

    /**
     * Lista todas as matrículas ativas
     */
    public List<MatriculaDTO> listarAtivas() {
        log.info("Listando matrículas ativas");
        return matriculaRepository.findByStatusOrderByDataMatriculaDesc(Matricula.Status.ATIVA)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista matrículas de uma turma
     */
    public List<MatriculaDTO> listarPorTurma(Integer turmaId) {
        log.info("Listando matrículas da turma: {}", turmaId);
        return matriculaRepository.findByTurmaIdAndStatusOrderByDataMatricula(turmaId, Matricula.Status.ATIVA)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Registra uma nova matrícula
     * Adiciona à fila do Redis para processamento assíncrono
     */
    @Transactional
    public MatriculaDTO registrarMatricula(MatriculaDTO matriculaDTO) {
        log.info("Registrando matrícula para aluno: {}", matriculaDTO.getAlunoId());

        // Validar aluno
        Aluno aluno = alunoRepository.findById(matriculaDTO.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // Validar turma
        Turma turma = turmaRepository.findById(matriculaDTO.getTurmaId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        // Verificar se aluno já tem matrícula ativa
        if (matriculaRepository.existsByAlunoIdAndTurmaIdAndStatus(
                matriculaDTO.getAlunoId(), matriculaDTO.getTurmaId(), Matricula.Status.ATIVA)) {
            throw new RuntimeException("Aluno já possui matrícula ativa nesta turma");
        }

        // Verificar capacidade da turma
        long ocupacao = matriculaRepository.countByTurmaIdAndStatus(matriculaDTO.getTurmaId(), Matricula.Status.ATIVA);
        if (ocupacao >= turma.getCapacidade()) {
            throw new RuntimeException("Turma sem vagas disponíveis");
        }

        // Criar matrícula
        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setTurno(Matricula.Turno.valueOf(matriculaDTO.getTurno()));
        matricula.setStatus(Matricula.Status.ATIVA);

        Matricula matriculaSalva = matriculaRepository.save(matricula);

        // Atualizar aluno com turma
        aluno.setTurma(turma);
        aluno.setTurno(Aluno.Turno.valueOf(matriculaDTO.getTurno()));
        alunoRepository.save(aluno);

        // Adicionar à fila do Redis
        adicionarFilaMatricula(matriculaSalva);

        log.info("Matrícula registrada com sucesso: ID {}", matriculaSalva.getId());

        return converterParaDTO(matriculaSalva);
    }

    /**
     * Cancela uma matrícula
     * Adiciona à fila do Redis para processamento assíncrono
     */
    @Transactional
    public void cancelarMatricula(Integer matriculaId, String motivo) {
        log.info("Cancelando matrícula: {}", matriculaId);

        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));

        if (!matricula.getStatus().equals(Matricula.Status.ATIVA)) {
            throw new RuntimeException("Apenas matrículas ativas podem ser canceladas");
        }

        matricula.setStatus(Matricula.Status.CANCELADA);
        matricula.setDataCancelamento(LocalDateTime.now());
        matricula.setMotivoCancelamento(motivo);

        matriculaRepository.save(matricula);

        // Atualizar aluno
        Aluno aluno = matricula.getAluno();
        aluno.setStatus(Aluno.Status.INATIVO);
        aluno.setTurma(null);
        alunoRepository.save(aluno);

        // Adicionar à fila do Redis
        adicionarFilaCancelamento(matricula);

        log.info("Matrícula cancelada com sucesso: ID {}", matriculaId);
    }

    /**
     * Adiciona uma matrícula à fila do Redis
     */
    private void adicionarFilaMatricula(Matricula matricula) {
        try {
            String mensagem = String.format("Matrícula ID: %d, Aluno: %s, Turma: %s",
                    matricula.getId(),
                    matricula.getAluno().getNome(),
                    matricula.getTurma().getNome());

            redisTemplate.opsForList().rightPush(FILA_MATRICULAS_KEY, mensagem);
            log.info("Matrícula adicionada à fila Redis: {}", mensagem);
        } catch (Exception e) {
            log.error("Erro ao adicionar matrícula à fila Redis", e);
        }
    }

    /**
     * Adiciona um cancelamento à fila do Redis
     */
    private void adicionarFilaCancelamento(Matricula matricula) {
        try {
            String mensagem = String.format("Cancelamento ID: %d, Aluno: %s, Motivo: %s",
                    matricula.getId(),
                    matricula.getAluno().getNome(),
                    matricula.getMotivoCancelamento());

            redisTemplate.opsForList().rightPush(FILA_CANCELAMENTOS_KEY, mensagem);
            log.info("Cancelamento adicionado à fila Redis: {}", mensagem);
        } catch (Exception e) {
            log.error("Erro ao adicionar cancelamento à fila Redis", e);
        }
    }

    /**
     * Obtém o tamanho da fila de matrículas
     */
    public long obterTamanhFilaMatriculas() {
        Long tamanho = redisTemplate.opsForList().size(FILA_MATRICULAS_KEY);
        return tamanho != null ? tamanho : 0;
    }

    /**
     * Obtém o tamanho da fila de cancelamentos
     */
    public long obterTamanhFilaCancelamentos() {
        Long tamanho = redisTemplate.opsForList().size(FILA_CANCELAMENTOS_KEY);
        return tamanho != null ? tamanho : 0;
    }

    /**
     * Converte uma Matricula para MatriculaDTO
     */
    private MatriculaDTO converterParaDTO(Matricula matricula) {
        return MatriculaDTO.builder()
                .id(matricula.getId())
                .alunoId(matricula.getAluno().getId())
                .alunoNome(matricula.getAluno().getNome())
                .alunoCpf(matricula.getAluno().getCpf())
                .turmaId(matricula.getTurma().getId())
                .turmaNome(matricula.getTurma().getNome())
                .turmaSerie(matricula.getTurma().getSerie())
                .turno(matricula.getTurno().toString())
                .status(matricula.getStatus().toString())
                .dataMatricula(matricula.getDataMatricula())
                .dataCancelamento(matricula.getDataCancelamento())
                .motivoCancelamento(matricula.getMotivoCancelamento())
                .dataAtualizacao(matricula.getDataAtualizacao())
                .build();
    }
}
