package br.com.matricula;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}