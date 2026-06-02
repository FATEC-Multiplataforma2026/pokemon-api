package io.github.mrspock182.pokemon.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record Power(
        @JsonProperty("name") String name,
        @JsonProperty("strength") BigDecimal strength
) {
}