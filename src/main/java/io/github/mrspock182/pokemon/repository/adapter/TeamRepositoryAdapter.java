package io.github.mrspock182.pokemon.repository.adapter;

import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.repository.orm.CapturePokemonOrm;

public class TeamRepositoryAdapter {
    private TeamRepositoryAdapter() {
    }

    public static UserPokemon cast(CapturePokemonOrm orm) {
        return new UserPokemon(
                orm.getId(),
                orm.getUserId(),
                orm.getTeam(),
                orm.getOptions());
    }
}