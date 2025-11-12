package br.com.matricula.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarSenha {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senha = "admin123"; // coloque aqui a senha que você quer
        String hash = encoder.encode(senha);
        System.out.println("Hash gerada: " + hash);
    }
}
