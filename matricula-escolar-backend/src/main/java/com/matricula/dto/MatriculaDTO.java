package com.matricula.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO: MatriculaDTO
 * Transferência de dados de matrícula
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaDTO {

    private Integer id;

    @NotNull(message = "Aluno é obrigatório")
    private Integer alunoId;

    private String alunoNome;
    private String alunoCpf;

    @NotNull(message = "Turma é obrigatória")
    private Integer turmaId;

    private String turmaNome;
    private String turmaSerie;

    @NotNull(message = "Turno é obrigatório")
    private String turno;

    private String status;
    private LocalDateTime dataMatricula;
    private LocalDateTime dataCancelamento;
    private String motivoCancelamento;
    private LocalDateTime dataAtualizacao;
}
