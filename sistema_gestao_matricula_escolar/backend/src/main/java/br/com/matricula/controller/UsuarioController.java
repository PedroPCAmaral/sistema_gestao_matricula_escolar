package br.com.matricula.controller;

import br.com.matricula.model.Usuario;
import br.com.matricula.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public Usuario criar(@RequestBody Usuario dto) {
        if (dto.getIdUsuario() == null) {
            dto.setIdUsuario("USR" + System.currentTimeMillis());
        }
        dto.setSenhaHash(passwordEncoder.encode(dto.getSenhaHash()));
        return usuarioRepository.save(dto);
    }
}
