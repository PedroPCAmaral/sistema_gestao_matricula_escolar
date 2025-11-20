package com.matricula.controller;

import com.matricula.dto.TurmaDTO;
import com.matricula.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller: TurmaController
 * Endpoints para gerenciamento de turmas
 */
@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173", "file://"})
public class TurmaController {

    private final TurmaService turmaService;

    /**
     * Endpoint: GET /turmas
     * Lista todas as turmas ativas
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TurmaDTO>> listarTodas() {
        log.info("Listando todas as turmas");
        List<TurmaDTO> turmas = turmaService.listarTodas();
        return ResponseEntity.ok(turmas);
    }

    /**
     * Endpoint: GET /turmas/{id}
     * Busca uma turma por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            log.info("Buscando turma com ID: {}", id);
            TurmaDTO turma = turmaService.buscarPorId(id);
            return ResponseEntity.ok(turma);
        } catch (Exception e) {
            log.error("Erro ao buscar turma", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: POST /turmas
     * Cria uma nova turma
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criar(@Valid @RequestBody TurmaDTO turmaDTO) {
        try {
            log.info("Criando nova turma: {}", turmaDTO.getNome());
            TurmaDTO turmaNova = turmaService.criar(turmaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(turmaNova);
        } catch (Exception e) {
            log.error("Erro ao criar turma", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: PUT /turmas/{id}
     * Atualiza uma turma
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> atualizar(@PathVariable Integer id, @Valid @RequestBody TurmaDTO turmaDTO) {
        try {
            log.info("Atualizando turma com ID: {}", id);
            TurmaDTO turmaAtualizada = turmaService.atualizar(id, turmaDTO);
            return ResponseEntity.ok(turmaAtualizada);
        } catch (Exception e) {
            log.error("Erro ao atualizar turma", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: DELETE /turmas/{id}
     * Deleta uma turma
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletar(@PathVariable Integer id) {
        try {
            log.info("Deletando turma com ID: {}", id);
            turmaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar turma", e);
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
