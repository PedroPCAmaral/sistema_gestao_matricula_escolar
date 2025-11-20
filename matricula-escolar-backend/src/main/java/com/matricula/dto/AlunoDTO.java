package com.matricula.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO: AlunoDTO
 * Transferência de dados de aluno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoDTO {

    private Integer id;

    @NotBlank(message = "Nome do aluno é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    private String nome;

    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$", 
             message = "CPF deve estar no formato XXX.XXX.XXX-XX ou 11 dígitos")
    private String cpf;

    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", 
             message = "Telefone deve estar em um formato válido")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade não pode ser negativa")
    @Max(value = 150, message = "Idade não pode ser maior que 150")
    private Integer idade;

    @NotNull(message = "Data de nascimento é obrigatória")
    @PastOrPresent(message = "Data de nascimento não pode ser no futuro")
    private LocalDate dataNascimento;

    private String responsavelNome;
    private String responsavelTelefone;
    private String responsavelEmail;

    @NotNull(message = "Turma é obrigatória")
    private Integer turmaId;

    @NotNull(message = "Turno é obrigatório")
    private String turno;

    private String status;
    private LocalDateTime dataMatricula;
    private LocalDateTime dataAtualizacao;
}
