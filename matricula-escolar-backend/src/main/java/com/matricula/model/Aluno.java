package com.matricula.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade: Aluno
 * Descrição: Representa um aluno matriculado na escola
 * Campos obrigatórios: Nome, CPF, Telefone, Endereço, Idade, Turma, Turno
 */
@Entity
@Table(name = "alunos", indexes = {
    @Index(name = "idx_cpf", columnList = "cpf"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_turma_id", columnList = "turma_id"),
    @Index(name = "idx_turno", columnList = "turno"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_data_matricula", columnList = "data_matricula")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome do aluno é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúàâêôãõçÁÉÍÓÚÀÂÊÔÃÕÇ\\s]+$", 
             message = "Nome deve conter apenas letras e espaços")
    @Column(nullable = false, length = 150)
    private String nome;

    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$", 
             message = "CPF deve estar no formato XXX.XXX.XXX-XX ou 11 dígitos")
    @Column(unique = true, length = 14)
    private String cpf;

    @Email(message = "Email deve ser válido")
    @Column(length = 150)
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", 
             message = "Telefone deve estar em um formato válido")
    @Column(nullable = false, length = 20)
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String endereco;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade não pode ser negativa")
    @Max(value = 150, message = "Idade não pode ser maior que 150")
    @Column(nullable = false)
    private Integer idade;

    @NotNull(message = "Data de nascimento é obrigatória")
    @PastOrPresent(message = "Data de nascimento não pode ser no futuro")
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Size(max = 150, message = "Nome do responsável não pode exceder 150 caracteres")
    @Column(name = "responsavel_nome", length = 150)
    private String responsavelNome;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$|^$", 
             message = "Telefone do responsável deve estar em um formato válido")
    @Column(name = "responsavel_telefone", length = 20)
    private String responsavelTelefone;

    @Email(message = "Email do responsável deve ser válido")
    @Column(name = "responsavel_email", length = 150)
    private String responsavelEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @NotNull(message = "Turno é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ATIVO;

    @Column(name = "data_matricula", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime dataMatricula = LocalDateTime.now();

    @Column(name = "data_atualizacao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        dataMatricula = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public enum Turno {
        MATUTINO, VESPERTINO, NOTURNO
    }

    public enum Status {
        ATIVO, INATIVO, CANCELADO
    }
}
