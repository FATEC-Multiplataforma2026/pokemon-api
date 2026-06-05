package io.github.mrspock182.pokemon.integration.adapter;

import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.Power;
import io.github.mrspock182.pokemon.integration.dto.PokemonDetailResponse;

import java.math.BigDecimal;
import java.util.List;

public class PokemonIntegrationAdapter {
    private PokemonIntegrationAdapter() {}

    public static Pokemon cast(PokemonDetailResponse response) {
        List<String> types = response.getTypes().stream()
                .map(t -> t.getType().getName())
                .toList();

        List<Power> powers = response.getStats().stream()
                .map(s -> new Power(s.getStat().getName(), BigDecimal.valueOf(s.getBaseStat())))
                .toList();

        return new Pokemon(
                String.valueOf(response.getId()),
                response.getName(),
                response.getSprites().getFrontDefault(),
                types,
                powers
        );
    }
}
