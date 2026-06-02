package io.github.mrspock182.pokemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class PokemonApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PokemonApiApplication.class, args);
    }
}