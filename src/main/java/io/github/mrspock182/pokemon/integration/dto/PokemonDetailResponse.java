package io.github.mrspock182.pokemon.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokemonDetailResponse(
        int id,
        String name,
        Sprites sprites,
        List<TypeSlot> types,
        List<StatSlot> stats
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sprites(@JsonProperty("front_default") String frontDefault) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TypeSlot(TypeInfo type) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TypeInfo(String name) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StatSlot(
            @JsonProperty("base_stat") int baseStat,
            StatInfo stat
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record StatInfo(String name) {}
    }
}
