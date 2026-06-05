package io.github.mrspock182.pokemon.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonDetailResponse {
    private Integer id;
    private String name;
    private PokemonSpritesResponse sprites;
    private List<PokemonTypeSlotResponse> types;
    private List<PokemonStatSlotResponse> stats;

    public PokemonDetailResponse(
            Integer id,
            String name,
            PokemonSpritesResponse sprites,
            List<PokemonTypeSlotResponse> types,
            List<PokemonStatSlotResponse> stats) {
        this.id = id;
        this.name = name;
        this.sprites = sprites;
        this.types = types;
        this.stats = stats;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
