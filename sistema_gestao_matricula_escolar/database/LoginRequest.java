package br.com.matricula.dto;

public class LoginRequest {
    private String login; // Renamed from username for consistency
    private String password; // Renamed from senha for consistency

    public String getLogin() { return login; }
    public String getPassword() { return password; }
}