package io.github.mrspock182.pokemon.entity;

import java.util.List;

public record UserPokemon(
        String id,
        String userId,
        List<Pokemon> team,
        List<Pokemon> capture
) {
}