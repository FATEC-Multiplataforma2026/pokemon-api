package io.github.mrspock182.pokemon.resource.dto;

import java.util.List;

public record TeamUpdateRequest(
        List<String> teamOrder,
        String removedPokemon,
        String newPokemon
) {
}
