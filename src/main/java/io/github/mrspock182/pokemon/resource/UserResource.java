package io.github.mrspock182.pokemon.resource;

import io.github.mrspock182.pokemon.entity.User;
import io.github.mrspock182.pokemon.resource.dto.LoginRequest;
import io.github.mrspock182.pokemon.resource.dto.LoginResponse;
import io.github.mrspock182.pokemon.resource.dto.RegisterRequest;
import io.github.mrspock182.pokemon.resource.dto.UserStatsRequest;
import io.github.mrspock182.pokemon.resource.dto.UserStatsResponse;
import io.github.mrspock182.pokemon.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api-pokemon/auth/v1")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(CREATED)
    @PostMapping("/register")
    public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.username(), request.password());
        return new LoginResponse(user.id());
    }

    @ResponseStatus(OK)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String userId = userService.login(request.username(), request.password());
        return new LoginResponse(userId);
    }

    @ResponseStatus(OK)
    @GetMapping("/stats/{userId}")
    public UserStatsResponse getStats(@PathVariable String userId) {
        User user = userService.getStats(userId);
        return new UserStatsResponse(user.id(), user.username(), user.level(), user.vitorias(), user.derrotas());
    }

    @ResponseStatus(OK)
    @PutMapping("/stats/{userId}")
    public UserStatsResponse updateStats(@PathVariable String userId, @Valid @RequestBody UserStatsRequest request) {
        User user = userService.updateStats(userId, request.level(), request.vitorias(), request.derrotas());
        return new UserStatsResponse(user.id(), user.username(), user.level(), user.vitorias(), user.derrotas());
    }
}
