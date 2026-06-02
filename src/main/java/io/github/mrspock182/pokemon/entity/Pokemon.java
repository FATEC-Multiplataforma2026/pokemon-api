package io.github.mrspock182.pokemon.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Pokemon(
        @JsonProperty("index") String index,
        @JsonProperty("name") String name,
        @JsonProperty("image") String image,
        @JsonProperty("types") List<String> types,
        @JsonProperty("abilities") List<Power> abilities
) {
}