package io.github.mrspock182.pokemon.service;

import io.github.mrspock182.pokemon.entity.User;
import io.github.mrspock182.pokemon.exception.UnauthorizedException;
import io.github.mrspock182.pokemon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PokemonService pokemonService;

    public UserService(UserRepository userRepository, PokemonService pokemonService) {
        this.userRepository = userRepository;
        this.pokemonService = pokemonService;
    }

    public User register(String username, String password) {
        String passwordHash = hash(password);
        User user = userRepository.save(username, passwordHash);
        pokemonService.createTeam(user.id());
        return user;
    }

    public String login(String username, String password) {
        String passwordHash = hash(password);
        return userRepository.findByUsername(username)
                .filter(user -> user.passwordHash().equals(passwordHash))
                .map(User::id)
                .orElseThrow(() -> new UnauthorizedException("Usuário ou senha inválidos"));
    }

    private static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new InternalError("SHA-256 não disponível", ex);
        }
    }
}
