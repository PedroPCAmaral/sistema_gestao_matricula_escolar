package br.com.matricula.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Gerador de IDs personalizados para a entidade Aluno.
 * Utiliza a função SQL fnc_gerar_id_aluno() no banco de dados.
 */
public class AlunoIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return session.doReturningWork(connection -> {
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery("SELECT fnc_gerar_id_aluno()")) {

                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    throw new RuntimeException("Erro: função fnc_gerar_id_aluno() não retornou valor.");
                }

            } catch (Exception e) {
                throw new RuntimeException("Erro ao gerar ID do aluno: " + e.getMessage(), e);
            }
        });
    }
}
