package io.github.mrspock182.pokemon.integration;

import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.integration.adapter.PokemonIntegrationAdapter;
import io.github.mrspock182.pokemon.integration.client.PokeApiWithHttpClient;
import org.springframework.stereotype.Component;

@Component
public class PokemonIntegration {
    private final PokeApiWithHttpClient pokeApiClient;

    public PokemonIntegration(PokeApiWithHttpClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
    }

    public Pokemon findPokemonById(final Integer id) {
        return PokemonIntegrationAdapter.cast(pokeApiClient.getPokemon(id));
    }
}
