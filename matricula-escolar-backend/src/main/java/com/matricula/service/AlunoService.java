package com.matricula.service;

import com.matricula.dto.AlunoDTO;
import com.matricula.model.Aluno;
import com.matricula.model.Turma;
import com.matricula.repository.AlunoRepository;
import com.matricula.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço: AlunoService
 * Responsável pela lógica de negócio de alunos
 * Implementa CRUD completo com validações
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;

    /**
     * Lista todos os alunos ativos
     */
    public List<AlunoDTO> listarTodos() {
        log.info("Listando todos os alunos");
        return alunoRepository.findAllAtivos()
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um aluno por ID
     */
    public AlunoDTO buscarPorId(Integer id) {
        log.info("Buscando aluno com ID: {}", id);
        return alunoRepository.findById(id)
                .map(this::converterParaDTO)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    /**
     * Lista alunos de uma turma
     */
    public List<AlunoDTO> listarPorTurma(Integer turmaId) {
        log.info("Listando alunos da turma: {}", turmaId);
        return alunoRepository.findByTurmaIdAndStatusOrderByNome(turmaId, Aluno.Status.ATIVO)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cria um novo aluno
     */
    @Transactional
    public AlunoDTO criar(AlunoDTO alunoDTO) {
        log.info("Criando novo aluno: {}", alunoDTO.getNome());

        // Validações
        if (alunoRepository.findByCpf(alunoDTO.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (alunoDTO.getEmail() != null && alunoRepository.findByEmail(alunoDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(alunoDTO.getNome());
        aluno.setCpf(alunoDTO.getCpf());
        aluno.setEmail(alunoDTO.getEmail());
        aluno.setTelefone(alunoDTO.getTelefone());
        aluno.setEndereco(alunoDTO.getEndereco());
        aluno.setIdade(alunoDTO.getIdade());
        aluno.setDataNascimento(alunoDTO.getDataNascimento());
        aluno.setResponsavelNome(alunoDTO.getResponsavelNome());
        aluno.setResponsavelTelefone(alunoDTO.getResponsavelTelefone());
        aluno.setResponsavelEmail(alunoDTO.getResponsavelEmail());
        aluno.setTurno(Aluno.Turno.valueOf(alunoDTO.getTurno()));
        aluno.setStatus(Aluno.Status.ATIVO);

        // Associar turma se fornecida
        if (alunoDTO.getTurmaId() != null) {
            Turma turma = turmaRepository.findById(alunoDTO.getTurmaId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            aluno.setTurma(turma);
        }

        Aluno alunoSalvo = alunoRepository.save(aluno);
        log.info("Aluno criado com sucesso: ID {}", alunoSalvo.getId());

        return converterParaDTO(alunoSalvo);
    }

    /**
     * Atualiza um aluno existente
     */
    @Transactional
    public AlunoDTO atualizar(Integer id, AlunoDTO alunoDTO) {
        log.info("Atualizando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setNome(alunoDTO.getNome());
        aluno.setEmail(alunoDTO.getEmail());
        aluno.setTelefone(alunoDTO.getTelefone());
        aluno.setEndereco(alunoDTO.getEndereco());
        aluno.setIdade(alunoDTO.getIdade());
        aluno.setDataNascimento(alunoDTO.getDataNascimento());
        aluno.setResponsavelNome(alunoDTO.getResponsavelNome());
        aluno.setResponsavelTelefone(alunoDTO.getResponsavelTelefone());
        aluno.setResponsavelEmail(alunoDTO.getResponsavelEmail());
        aluno.setTurno(Aluno.Turno.valueOf(alunoDTO.getTurno()));

        if (alunoDTO.getTurmaId() != null) {
            Turma turma = turmaRepository.findById(alunoDTO.getTurmaId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
            aluno.setTurma(turma);
        }

        Aluno alunoAtualizado = alunoRepository.save(aluno);
        log.info("Aluno atualizado com sucesso: ID {}", id);

        return converterParaDTO(alunoAtualizado);
    }

    /**
     * Deleta um aluno (soft delete - marca como inativo)
     */
    @Transactional
    public void deletar(Integer id) {
        log.info("Deletando aluno com ID: {}", id);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setStatus(Aluno.Status.CANCELADO);
        alunoRepository.save(aluno);

        log.info("Aluno deletado com sucesso: ID {}", id);
    }

    /**
     * Converte um Aluno para AlunoDTO
     */
    private AlunoDTO converterParaDTO(Aluno aluno) {
        return AlunoDTO.builder()
                .id(aluno.getId())
                .nome(aluno.getNome())
                .cpf(aluno.getCpf())
                .email(aluno.getEmail())
                .telefone(aluno.getTelefone())
                .endereco(aluno.getEndereco())
                .idade(aluno.getIdade())
                .dataNascimento(aluno.getDataNascimento())
                .responsavelNome(aluno.getResponsavelNome())
                .responsavelTelefone(aluno.getResponsavelTelefone())
                .responsavelEmail(aluno.getResponsavelEmail())
                .turmaId(aluno.getTurma() != null ? aluno.getTurma().getId() : null)
                .turno(aluno.getTurno().toString())
                .status(aluno.getStatus().toString())
                .dataMatricula(aluno.getDataMatricula())
                .dataAtualizacao(aluno.getDataAtualizacao())
                .build();
    }
}
