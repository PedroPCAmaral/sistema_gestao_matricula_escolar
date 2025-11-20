package com.matricula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal da aplicação Spring Boot
 * Gerenciamento de Matrículas Escolares
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.matricula"})
public class MatriculaEscolarApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatriculaEscolarApplication.class, args);
    }
}
