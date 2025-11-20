package com.matricula.controller;

import com.matricula.dto.AlunoDTO;
import com.matricula.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller: AlunoController
 * Endpoints para gerenciamento de alunos
 */
@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173", "file://"})
public class AlunoController {

    private final AlunoService alunoService;

    /**
     * Endpoint: GET /alunos
     * Lista todos os alunos
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AlunoDTO>> listarTodos() {
        log.info("Listando todos os alunos");
        List<AlunoDTO> alunos = alunoService.listarTodos();
        return ResponseEntity.ok(alunos);
    }

    /**
     * Endpoint: GET /alunos/{id}
     * Busca um aluno por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            log.info("Buscando aluno com ID: {}", id);
            AlunoDTO aluno = alunoService.buscarPorId(id);
            return ResponseEntity.ok(aluno);
        } catch (Exception e) {
            log.error("Erro ao buscar aluno", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: GET /alunos/turma/{turmaId}
     * Lista alunos de uma turma
     */
    @GetMapping("/turma/{turmaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AlunoDTO>> listarPorTurma(@PathVariable Integer turmaId) {
        log.info("Listando alunos da turma: {}", turmaId);
        List<AlunoDTO> alunos = alunoService.listarPorTurma(turmaId);
        return ResponseEntity.ok(alunos);
    }

    /**
     * Endpoint: POST /alunos
     * Cria um novo aluno
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criar(@Valid @RequestBody AlunoDTO alunoDTO) {
        try {
            log.info("Criando novo aluno: {}", alunoDTO.getNome());
            AlunoDTO alunoNovo = alunoService.criar(alunoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(alunoNovo);
        } catch (Exception e) {
            log.error("Erro ao criar aluno", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: PUT /alunos/{id}
     * Atualiza um aluno
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody AlunoDTO alunoDTO) {
        try {
            log.info("Atualizando aluno com ID: {}", id);
            AlunoDTO alunoAtualizado = alunoService.atualizar(id, alunoDTO);
            return ResponseEntity.ok(alunoAtualizado);
        } catch (Exception e) {
            log.error("Erro ao atualizar aluno", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: DELETE /alunos/{id}
     * Deleta um aluno
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            log.info("Deletando aluno com ID: {}", id);
            alunoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar aluno", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Classe auxiliar para resposta de erro
     */
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
