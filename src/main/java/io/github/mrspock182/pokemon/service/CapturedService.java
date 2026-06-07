package io.github.mrspock182.pokemon.service;

import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.integration.PokemonIntegration;
import io.github.mrspock182.pokemon.repository.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class CapturedService {

    private final PokemonIntegration pokemonIntegration;
    private final TeamRepository teamRepository;

    public CapturedService(PokemonIntegration pokemonIntegration, TeamRepository teamRepository) {
        this.pokemonIntegration = pokemonIntegration;
        this.teamRepository = teamRepository;
    }

    public UserPokemon addPokemon(String userId, Integer pokemonId) {
        Pokemon pokemon = pokemonIntegration.findPokemonById(pokemonId);
        return teamRepository.addCapture(userId, pokemon);
    }

    public UserPokemon removePokemon(String userId, String index) {
        return teamRepository.removeCapture(userId, index);
    }

}
