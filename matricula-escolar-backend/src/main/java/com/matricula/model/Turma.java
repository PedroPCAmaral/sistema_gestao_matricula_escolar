package com.matricula.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade: Turma
 * Descrição: Representa uma turma na escola
 */
@Entity
@Table(name = "turmas", indexes = {
    @Index(name = "idx_serie", columnList = "serie"),
    @Index(name = "idx_turno", columnList = "turno"),
    @Index(name = "idx_ativo", columnList = "ativo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome da turma é obrigatório")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Série é obrigatória")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String serie;

    @NotNull(message = "Turno é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser no mínimo 1")
    @Max(value = 100, message = "Capacidade não pode exceder 100")
    @Column(nullable = false)
    @Builder.Default
    private Integer capacidade = 30;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Usuario professor;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public enum Turno {
        MATUTINO, VESPERTINO, NOTURNO
    }
}
