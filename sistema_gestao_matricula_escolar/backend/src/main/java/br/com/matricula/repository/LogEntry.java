package br.com.matricula.model.log;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "audit_logs")
public class LogEntry {

    @Id
    private String id;
    private String acao; // Ex: "CRIAR", "ATUALIZAR", "DELETAR"
    private String entidade; // Ex: "Aluno"
    private String entidadeId;
    private String usuario;
    private Instant timestamp;
    private Object detalhes; // Pode guardar detalhes da alteração

    public LogEntry(String acao, String entidade, String entidadeId, String usuario, Object detalhes) {
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.usuario = usuario;
        this.detalhes = detalhes;
        this.timestamp = Instant.now();
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }
    public String getEntidadeId() { return entidadeId; }
    public void setEntidadeId(String entidadeId) { this.entidadeId = entidadeId; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public Object getDetalhes() { return detalhes; }
    public void setDetalhes(Object detalhes) { this.detalhes = detalhes; }
}