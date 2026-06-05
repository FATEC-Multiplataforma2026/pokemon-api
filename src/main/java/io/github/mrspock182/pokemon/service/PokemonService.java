package io.github.mrspock182.pokemon.service;

import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.integration.PokemonIntegration;
import io.github.mrspock182.pokemon.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PokemonService {
    private static final int TOTAL_POKEMONS = 151;
    private static final int TEAM_SIZE = 5;

    private final PokemonIntegration pokeApiGateway;
    private final TeamRepository teamRepository;

    public PokemonService(PokemonIntegration pokeApiGateway, TeamRepository teamRepository) {
        this.pokeApiGateway = pokeApiGateway;
        this.teamRepository = teamRepository;
    }

    public UserPokemon createTeam(String userId) {
        return teamRepository.save(userId, generateTeam());
    }

    private List<Pokemon> generateTeam() {
        List<Integer> ids = new ArrayList<>();
        while (ids.size() < TEAM_SIZE) {
            int id = ThreadLocalRandom.current().nextInt(1, TOTAL_POKEMONS + 1);
            if (!ids.contains(id)) {
                ids.add(id);
            }
        }
        List<CompletableFuture<Pokemon>> futures = ids.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> pokeApiGateway.findPokemonById(id)))
                .toList();
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}