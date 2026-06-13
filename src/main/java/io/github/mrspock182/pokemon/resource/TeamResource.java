package io.github.mrspock182.pokemon.resource;

import io.github.mrspock182.pokemon.entity.TeamUpdate;
import io.github.mrspock182.pokemon.repository.TeamRepository;
import io.github.mrspock182.pokemon.resource.adapter.TeamResourceAdapter;
import io.github.mrspock182.pokemon.resource.dto.TeamUpdateRequest;
import io.github.mrspock182.pokemon.resource.dto.UserPokemonResponse;
import io.github.mrspock182.pokemon.service.TeamService;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api-pokemon/pokemon/v1")
public class TeamResource {
    private final TeamService teamService;
    private final TeamRepository teamRepository;

    public TeamResource(TeamService teamService, TeamRepository teamRepository) {
        this.teamService = teamService;
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
            @RequestBody TeamUpdateRequest request) {
        TeamUpdate teamUpdate = TeamResourceAdapter.cast(request);
        return TeamResourceAdapter.cast(teamService.changeTeam(userId, teamUpdate));
    }
}