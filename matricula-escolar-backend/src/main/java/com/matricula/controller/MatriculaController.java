package com.matricula.controller;

import com.matricula.dto.MatriculaDTO;
import com.matricula.service.MatriculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controller: MatriculaController
 * Endpoints para gerenciamento de matrículas
 */
@RestController
@RequestMapping("/matriculas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173", "file://"})
public class MatriculaController {

    private final MatriculaService matriculaService;

    /**
     * Endpoint: GET /matriculas
     * Lista todas as matrículas ativas
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MatriculaDTO>> listarAtivas() {
        log.info("Listando matrículas ativas");
        List<MatriculaDTO> matriculas = matriculaService.listarAtivas();
        return ResponseEntity.ok(matriculas);
    }

    /**
     * Endpoint: GET /matriculas/turma/{turmaId}
     * Lista matrículas de uma turma
     */
    @GetMapping("/turma/{turmaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MatriculaDTO>> listarPorTurma(@PathVariable Integer turmaId) {
        log.info("Listando matrículas da turma: {}", turmaId);
        List<MatriculaDTO> matriculas = matriculaService.listarPorTurma(turmaId);
        return ResponseEntity.ok(matriculas);
    }

    /**
     * Endpoint: POST /matriculas
     * Registra uma nova matrícula
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registrarMatricula(@Valid @RequestBody MatriculaDTO matriculaDTO) {
        try {
            log.info("Registrando matrícula para aluno: {}", matriculaDTO.getAlunoId());
            MatriculaDTO matriculaNova = matriculaService.registrarMatricula(matriculaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(matriculaNova);
        } catch (Exception e) {
            log.error("Erro ao registrar matrícula", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: DELETE /matriculas/{id}
     * Cancela uma matrícula
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelarMatricula(@PathVariable Integer id, 
                                               @RequestBody Map<String, String> request) {
        try {
            String motivo = request.getOrDefault("motivo", "Cancelamento solicitado");
            log.info("Cancelando matrícula: {}", id);
            matriculaService.cancelarMatricula(id, motivo);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao cancelar matrícula", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint: GET /matriculas/fila/status
     * Obtém o status das filas do Redis
     */
    @GetMapping("/fila/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obterStatusFila() {
        try {
            log.info("Obtendo status das filas");
            long filaMatriculas = matriculaService.obterTamanhFilaMatriculas();
            long filaCancelamentos = matriculaService.obterTamanhFilaCancelamentos();

            Map<String, Object> status = Map.of(
                    "filaMatriculas", filaMatriculas,
                    "filaCancelamentos", filaCancelamentos,
                    "totalFila", filaMatriculas + filaCancelamentos
            );

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Erro ao obter status da fila", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
