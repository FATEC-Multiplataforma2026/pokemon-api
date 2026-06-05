package io.github.mrspock182.pokemon.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonListResponse {
    private List<PokemonItemResponse> results;

    public PokemonListResponse(List<PokemonItemResponse> results) {
        this.results = results;
    }

    public List<PokemonItemResponse> getResults() {
        return results;
    }

    public void setResults(List<PokemonItemResponse> results) {
        this.results = results;
    }
}
