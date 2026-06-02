package io.github.mrspock182.pokemon.integration.adapter;

import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.Power;
import io.github.mrspock182.pokemon.integration.dto.PokemonDetailResponse;

import java.math.BigDecimal;
import java.util.List;

public class PokemonIntegrationAdapter {
    private PokemonIntegrationAdapter() {}

    public static Pokemon cast(PokemonDetailResponse response) {
        List<String> types = response.types().stream()
                .map(t -> t.type().name())
                .toList();

        List<Power> powers = response.stats().stream()
                .map(s -> new Power(s.stat().name(), BigDecimal.valueOf(s.baseStat())))
                .toList();

        return new Pokemon(
                String.valueOf(response.id()),
                response.name(),
                response.sprites().frontDefault(),
                types,
                powers
        );
    }
}
