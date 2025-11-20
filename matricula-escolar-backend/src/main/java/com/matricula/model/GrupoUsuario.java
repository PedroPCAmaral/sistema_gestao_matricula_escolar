package com.matricula.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade: GrupoUsuario
 * Descrição: Define grupos de usuários com diferentes níveis de permissão
 * Justificativa: Controle granular de acesso baseado em grupos
 */
@Entity
@Table(name = "grupos_usuarios", indexes = {
    @Index(name = "idx_nome", columnList = "nome"),
    @Index(name = "idx_ativo", columnList = "ativo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome do grupo é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "JSON", nullable = false)
    @Builder.Default
    private String permissoes = "[]";

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
}
