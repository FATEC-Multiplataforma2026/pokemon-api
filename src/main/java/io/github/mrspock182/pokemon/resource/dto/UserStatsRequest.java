package io.github.mrspock182.pokemon.resource.dto;

import jakarta.validation.constraints.Min;

public record UserStatsRequest(
        @Min(value = 1, message = "Level deve ser no mínimo 1")
        int level,

        @Min(value = 0, message = "Vitórias não pode ser negativo")
        int vitorias,

        @Min(value = 0, message = "Derrotas não pode ser negativo")
        int derrotas
) {
}
