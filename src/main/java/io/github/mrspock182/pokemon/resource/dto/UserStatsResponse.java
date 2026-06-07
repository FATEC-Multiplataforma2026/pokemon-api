package io.github.mrspock182.pokemon.resource.dto;

public record UserStatsResponse(String userId, String username, int level, int vitorias, int derrotas) {
}
