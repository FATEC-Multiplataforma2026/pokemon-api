package io.github.mrspock182.pokemon.entity;

import java.util.List;

public record TeamUpdate(
        List<String> teamOrder,
        String removedPokemon,
        String newPokemon
) {
}
