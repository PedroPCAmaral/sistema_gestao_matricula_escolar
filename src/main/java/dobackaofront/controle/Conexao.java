package br.com.matricula;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // Prioriza variáveis de ambiente, mas usa valores padrão para desenvolvimento local
    private static final String DB_URL = getEnvOrDefault("DB_URL", "jdbc:mysql://localhost:3306/matricula_escolar?useSSL=false&serverTimezone=UTC");
    private static final String DB_USER = getEnvOrDefault("DB_USER", "root");
    private static final String DB_PASS = getEnvOrDefault("DB_PASS", "#Mae22150");

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        try {
            // ⚙️ Ajuste 2: Garante que o driver do MySQL seja carregado
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado!", e);
        }
    }
}