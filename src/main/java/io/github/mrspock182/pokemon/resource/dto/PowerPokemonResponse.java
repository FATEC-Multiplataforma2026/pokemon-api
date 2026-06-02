package io.github.mrspock182.pokemon.resource.dto;

import java.math.BigDecimal;

public record PowerPokemonResponse(
        String name,
        BigDecimal strength
) {
}
