package io.github.mrspock182.pokemon.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonDetailResponse {
    private int id;
    private String name;
    private PokemonSpritesResponse sprites;
    private List<PokemonTypeSlotResponse> types;
    private List<PokemonStatSlotResponse> stats;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PokemonSpritesResponse getSprites() {
        return sprites;
    }

    public void setSprites(PokemonSpritesResponse sprites) {
        this.sprites = sprites;
    }

    public List<PokemonTypeSlotResponse> getTypes() {
        return types;
    }

    public void setTypes(List<PokemonTypeSlotResponse> types) {
        this.types = types;
    }

    public List<PokemonStatSlotResponse> getStats() {
        return stats;
    }

    public void setStats(List<PokemonStatSlotResponse> stats) {
        this.stats = stats;
    }
}
