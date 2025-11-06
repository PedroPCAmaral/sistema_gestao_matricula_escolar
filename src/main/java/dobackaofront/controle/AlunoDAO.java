package br.com.matricula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    public void cadastrar(Aluno aluno) {
        String sql = "INSERT INTO aluno (nome, cpf, idade, serie, turno, telefone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, aluno.getNome());
            ps.setString(2, aluno.getCpf());
            ps.setInt(3, aluno.getIdade());
            ps.setString(4, aluno.getSerie());
            ps.setString(5, aluno.getTurno());
            ps.setString(6, aluno.getTelefone());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao cadastrar aluno no banco de dados.", e);
        }
    }

    public List<Aluno> listar() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, idade, serie, turno, telefone FROM aluno";
        try (Connection con = Conexao.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Aluno aluno = mapRowToAluno(rs);
                alunos.add(aluno);
            }
        }
 catch (SQLException e) {
            throw new DataAccessException("Erro ao listar alunos do banco de dados.", e);
        }
        return alunos;
    }

    public Aluno buscarPorCpf(String cpf) {
        String sql = "SELECT id, nome, cpf, idade, serie, turno, telefone FROM aluno WHERE cpf = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAluno(rs);
                }
            }
        }
 catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar aluno por CPF no banco de dados.", e);
        }
        return null; // Retorna null se não encontrar
    }

    public int editar(Aluno aluno) {
        String sql = "UPDATE aluno SET nome = ?, idade = ?, serie = ?, turno = ?, telefone = ? WHERE cpf = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, aluno.getNome());
            ps.setInt(2, aluno.getIdade());
            ps.setString(3, aluno.getSerie());
            ps.setString(4, aluno.getTurno());
            ps.setString(5, aluno.getTelefone());
            ps.setString(6, aluno.getCpf());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao editar aluno no banco de dados.", e);
        }
    }

    public int remover(String cpf) {
        String sql = "DELETE FROM aluno WHERE cpf = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cpf);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao remover aluno do banco de dados.", e);
        }
    }

    /**
     * Método auxiliar para mapear uma linha do ResultSet para um objeto Aluno.
     * Evita repetição de código nos métodos de consulta.
     */
    private Aluno mapRowToAluno(ResultSet rs) throws SQLException {
        Aluno aluno = new Aluno();
        aluno.setId(rs.getLong("id"));
        aluno.setNome(rs.getString("nome"));
        aluno.setCpf(rs.getString("cpf"));
        aluno.setIdade(rs.getInt("idade"));
        aluno.setSerie(rs.getString("serie"));
        aluno.setTurno(rs.getString("turno"));
        aluno.setTelefone(rs.getString("telefone"));
        return aluno;
    }
}