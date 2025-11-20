package com.matricula.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entidade: Usuario
 * Descrição: Representa um usuário do sistema com informações pessoais e de acesso
 * Validações: Nome e Email são tratados com validações específicas
 */
@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_cpf", columnList = "cpf"),
    @Index(name = "idx_ativo", columnList = "ativo"),
    @Index(name = "idx_grupo_usuario_id", columnList = "grupo_usuario_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúàâêôãõçÁÉÍÓÚÀÂÊÔÃÕÇ\\s]+$", 
             message = "Nome deve conter apenas letras e espaços")
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$", 
             message = "CPF deve estar no formato XXX.XXX.XXX-XX ou 11 dígitos")
    @Column(unique = true, length = 14)
    private String cpf;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", 
             message = "Telefone deve estar em um formato válido")
    @Column(length = 20)
    private String telefone;

    @Column(columnDefinition = "TEXT")
    private String endereco;

    @Min(value = 0, message = "Idade não pode ser negativa")
    @Max(value = 150, message = "Idade não pode ser maior que 150")
    private Integer idade;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Column(nullable = false, length = 255)
    private String senhaHash;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grupo_usuario_id", nullable = false)
    private GrupoUsuario grupoUsuario;

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

    /**
     * Valida se o nome contém apenas caracteres válidos
     * @return true se o nome é válido
     */
    public boolean isNomeValido() {
        return nome != null && 
               nome.matches("^[a-zA-ZáéíóúàâêôãõçÁÉÍÓÚÀÂÊÔÃÕÇ\\s]+$") &&
               nome.length() >= 3 && 
               nome.length() <= 150;
    }

    /**
     * Valida se o email está em um formato correto
     * @return true se o email é válido
     */
    public boolean isEmailValido() {
        return email != null && 
               email.matches("^[A-Za-z0-9+_.-]+@(.+)$") &&
               email.length() <= 150;
    }

    /**
     * Limpa e normaliza o nome (remove espaços extras)
     */
    public void normalizarNome() {
        if (nome != null) {
            nome = nome.trim().replaceAll("\\s+", " ");
        }
    }

    /**
     * Normaliza o email para minúsculas
     */
    public void normalizarEmail() {
        if (email != null) {
            email = email.toLowerCase().trim();
        }
    }
}
