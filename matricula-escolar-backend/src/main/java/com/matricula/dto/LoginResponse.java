package com.matricula.dto;

import lombok.*;

/**
 * DTO: LoginResponse
 * Resposta de login com token JWT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String email;
    private String nome;
    private Integer usuarioId;
    private String grupoUsuario;
}
