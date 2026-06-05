package io.github.mrspock182.pokemon.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTypeSlotResponse {
    private PokemonTypeInfoResponse type;

    public PokemonTypeInfoResponse getType() {
        return type;
    }

    public void setType(PokemonTypeInfoResponse type) {
        this.type = type;
    }
}
