package io.github.mrspock182.pokemon.entity;

public record User(String id, String username, String passwordHash, int level, int vitorias, int derrotas) {
}
