package io.github.mrspock182.pokemon.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokemonListResponse(List<PokemonItem> results) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PokemonItem(String name, String url) {}
}
