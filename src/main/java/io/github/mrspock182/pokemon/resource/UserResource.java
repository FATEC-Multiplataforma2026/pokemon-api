package io.github.mrspock182.pokemon.resource;

import io.github.mrspock182.pokemon.entity.User;
import io.github.mrspock182.pokemon.resource.dto.LoginRequest;
import io.github.mrspock182.pokemon.resource.dto.LoginResponse;
import io.github.mrspock182.pokemon.resource.dto.RegisterRequest;
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
}
