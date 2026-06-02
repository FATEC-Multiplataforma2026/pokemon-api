package io.github.mrspock182.pokemon.resource;

import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.resource.adapter.TeamResourceAdapter;
import io.github.mrspock182.pokemon.resource.dto.UserPokemonResponse;
import io.github.mrspock182.pokemon.service.CapturedService;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api-pokemon/pokemon/v1")
public class CapturedResource {

    private final CapturedService capturedService;

    public CapturedResource(CapturedService capturedService) {
        this.capturedService = capturedService;
    }

    @ResponseStatus(OK)
    @PutMapping(path = "/captured")
    public UserPokemonResponse addPokemon(
            @RequestParam("user-id") String userId,
            @RequestParam("pokemon-id") Integer pokemonId) {
        UserPokemon userPokemon = capturedService.addPokemon(userId, pokemonId);
        return TeamResourceAdapter.cast(userPokemon);
    }

    @ResponseStatus(OK)
    @DeleteMapping(path = "/captured")
    public UserPokemonResponse removePokemon(
            @RequestParam("user-id") String userId,
            @RequestParam("index") Integer index) {
        UserPokemon userPokemon = capturedService.removePokemon(userId, index);
        return TeamResourceAdapter.cast(userPokemon);
    }
}
