package com.matricula.service;

import com.matricula.dto.LoginRequest;
import com.matricula.dto.LoginResponse;
import com.matricula.model.Usuario;
import com.matricula.repository.UsuarioRepository;
import com.matricula.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Serviço: AuthService
 * Responsável pela autenticação de usuários
 * Utiliza injeção de dependência para repositories e providers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Realiza login de um usuário
     * @param loginRequest Dados de login (email e senha)
     * @return LoginResponse com token JWT
     */
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Tentando login para usuário: {}", loginRequest.getEmail());

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue(loginRequest.getEmail());

        if (usuarioOpt.isEmpty()) {
            log.warn("Usuário não encontrado: {}", loginRequest.getEmail());
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar senha
        if (!passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenhaHash())) {
            log.warn("Senha inválida para usuário: {}", loginRequest.getEmail());
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        // Gerar token JWT
        String token = jwtTokenProvider.generateToken(usuario.getEmail());

        log.info("Login bem-sucedido para usuário: {}", usuario.getEmail());

        return LoginResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .usuarioId(usuario.getId())
                .grupoUsuario(usuario.getGrupoUsuario().getNome())
                .build();
    }

    /**
     * Valida um token JWT
     * @param token Token JWT
     * @return true se o token é válido
     */
    public boolean validarToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    /**
     * Extrai o email do token JWT
     * @param token Token JWT
     * @return Email do usuário
     */
    public String extrairEmailDoToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
}
