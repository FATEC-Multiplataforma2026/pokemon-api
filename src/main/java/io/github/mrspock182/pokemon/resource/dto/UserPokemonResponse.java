package io.github.mrspock182.pokemon.resource.dto;

import java.util.List;

public record UserPokemonResponse(
        String id,
        String userId,
        List<PokemonResponse> team,
        List<PokemonResponse> capture
) {
}