package br.com.matricula;
/**
 * Uma exceção não verificada (runtime) para encapsular erros de acesso a dados (como SQL).
 * Isso simplifica a camada de serviço, que não precisará mais de blocos try-catch para SQL.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}