package io.github.mrspock182.pokemon.resource;

import io.github.mrspock182.pokemon.repository.TeamRepository;
import io.github.mrspock182.pokemon.resource.adapter.TeamResourceAdapter;
import io.github.mrspock182.pokemon.resource.dto.UserPokemonResponse;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api-pokemon/pokemon/v1")
public class TeamResource {
    private final TeamRepository teamRepository;

    public TeamResource(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @ResponseStatus(OK)
    @GetMapping(path = "/team")
    public UserPokemonResponse getTeam(@RequestParam("user-id") String userId) {
        return TeamResourceAdapter.cast(teamRepository.findByUserId(userId));
    }

    @ResponseStatus(OK)
    @PutMapping(path = "/team")
    public UserPokemonResponse changeTeam(
            @RequestParam("user-id") String userId,
            @RequestParam("removed-pokemon") String removedPokemon,
            @RequestParam("new-pokemon") String newPokemon) {
        return TeamResourceAdapter.cast(teamRepository
                .update(userId, removedPokemon, newPokemon));
    }
}