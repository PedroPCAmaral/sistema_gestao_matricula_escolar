package com.matricula.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO: TurmaDTO
 * Transferência de dados de turma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurmaDTO {

    private Integer id;

    @NotBlank(message = "Nome da turma é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Série é obrigatória")
    @Size(max = 50)
    private String serie;

    @NotNull(message = "Turno é obrigatório")
    private String turno;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser no mínimo 1")
    @Max(value = 100, message = "Capacidade não pode exceder 100")
    private Integer capacidade;

    private Integer professorId;
    private String professorNome;

    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
