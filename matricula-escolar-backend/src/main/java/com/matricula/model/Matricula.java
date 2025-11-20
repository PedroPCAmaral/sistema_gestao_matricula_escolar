package com.matricula.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade: Matricula
 * Descrição: Registra as matrículas dos alunos em turmas
 */
@Entity
@Table(name = "matriculas", indexes = {
    @Index(name = "idx_aluno_id", columnList = "aluno_id"),
    @Index(name = "idx_turma_id", columnList = "turma_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_data_matricula", columnList = "data_matricula"),
    @Index(name = "uk_aluno_turma", columnList = "aluno_id,turma_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Aluno é obrigatório")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotNull(message = "Turma é obrigatória")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @NotNull(message = "Turno é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    @Column(name = "data_matricula", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime dataMatricula = LocalDateTime.now();

    @Column(name = "data_cancelamento")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataCancelamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ATIVA;

    @Column(name = "motivo_cancelamento", columnDefinition = "TEXT")
    private String motivoCancelamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id")
    private Usuario usuarioResponsavel;

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
        ATIVA, CANCELADA, SUSPENSA
    }
}
