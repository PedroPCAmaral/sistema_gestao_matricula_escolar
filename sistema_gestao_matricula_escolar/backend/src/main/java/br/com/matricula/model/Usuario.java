package br.com.matricula.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(generator = "usuario-id-generator")
    @GenericGenerator(
        name = "usuario-id-generator",
        strategy = "br.com.matricula.config.UsuarioIdGenerator" // Assumindo que você criará este gerador
    )
    @Column(name = "id_usuario")
    private String idUsuario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @ManyToOne
    @JoinColumn(name = "id_grupo")
    private GrupoUsuario grupo;

    // Getters e Setters
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public GrupoUsuario getGrupo() { return grupo; }
    public void setGrupo(GrupoUsuario grupo) { this.grupo = grupo; }
}
