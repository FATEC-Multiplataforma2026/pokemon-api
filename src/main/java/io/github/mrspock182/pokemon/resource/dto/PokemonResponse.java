package io.github.mrspock182.pokemon.resource.dto;

import java.util.List;

public record PokemonResponse(
        String index,
        String name,
        String image,
        List<String> types,
        List<PowerPokemonResponse> abilities
) {
}
