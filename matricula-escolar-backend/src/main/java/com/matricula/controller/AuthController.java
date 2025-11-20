package com.matricula.controller;

import com.matricula.dto.LoginRequest;
import com.matricula.dto.LoginResponse;
import com.matricula.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller: AuthController
 * Endpoints para autenticação
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173", "file://"})
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint: POST /auth/login
     * Realiza login de um usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Requisição de login para: {}", loginRequest.getEmail());
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao fazer login", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Erro ao fazer login: " + e.getMessage()));
        }
    }

    /**
     * Endpoint: GET /auth/validate
     * Valida um token JWT
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validarToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            boolean valido = authService.validarToken(jwt);
            return ResponseEntity.ok(new TokenValidationResponse(valido));
        } catch (Exception e) {
            log.error("Erro ao validar token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token inválido"));
        }
    }

    /**
     * Classe auxiliar para resposta de erro
     */
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    /**
     * Classe auxiliar para resposta de validação de token
     */
    public static class TokenValidationResponse {
        public boolean valido;

        public TokenValidationResponse(boolean valido) {
            this.valido = valido;
        }
    }
}
