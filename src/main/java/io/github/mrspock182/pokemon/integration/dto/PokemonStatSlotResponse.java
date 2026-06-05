package io.github.mrspock182.pokemon.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonStatSlotResponse {
    @JsonProperty("base_stat")
    private int baseStat;
    private PokemonStatInfoResponse stat;

    public PokemonStatSlotResponse(int baseStat, PokemonStatInfoResponse stat) {
        this.baseStat = baseStat;
        this.stat = stat;
    }

    public int getBaseStat() {
        return baseStat;
    }

    public void setBaseStat(int baseStat) {
        this.baseStat = baseStat;
    }

    public PokemonStatInfoResponse getStat() {
        return stat;
    }

    public void setStat(PokemonStatInfoResponse stat) {
        this.stat = stat;
    }
}