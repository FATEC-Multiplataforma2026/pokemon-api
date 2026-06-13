package io.github.mrspock182.pokemon.service;

import io.github.mrspock182.pokemon.entity.TeamUpdate;
import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.exception.BadRequestException;
import io.github.mrspock182.pokemon.repository.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public UserPokemon changeTeam(String userId, TeamUpdate teamUpdate) {
        if (teamUpdate.teamOrder() != null && !teamUpdate.teamOrder().isEmpty()) {
            return teamRepository.reorder(userId, teamUpdate.teamOrder());
        }
        if (teamUpdate.removedPokemon() != null && teamUpdate.newPokemon() != null) {
            return teamRepository.update(userId, teamUpdate.removedPokemon(), teamUpdate.newPokemon());
        }
        throw new BadRequestException("Informe 'teamOrder' para reordenar ou 'removedPokemon' e 'newPokemon' para substituir");
    }
}
