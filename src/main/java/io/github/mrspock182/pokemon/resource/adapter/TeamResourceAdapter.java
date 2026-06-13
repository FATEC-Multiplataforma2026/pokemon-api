package io.github.mrspock182.pokemon.resource.adapter;

import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.TeamUpdate;
import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.resource.dto.PokemonResponse;
import io.github.mrspock182.pokemon.resource.dto.PowerPokemonResponse;
import io.github.mrspock182.pokemon.resource.dto.TeamUpdateRequest;
import io.github.mrspock182.pokemon.resource.dto.UserPokemonResponse;

import java.util.List;
import java.util.Optional;

public class TeamResourceAdapter {
    private TeamResourceAdapter() {}

    public static TeamUpdate cast(TeamUpdateRequest request) {
        return new TeamUpdate(
                request.teamOrder(),
                request.removedPokemon(),
                request.newPokemon());
    }

    public static UserPokemonResponse cast(UserPokemon orm) {
        List<PokemonResponse> team = Optional.ofNullable(orm.team())
                .orElse(List.of())
                .stream()
                .map(TeamResourceAdapter::toPokemonResponse)
                .toList();

        List<PokemonResponse> capture = Optional.ofNullable(orm.capture())
                .orElse(List.of())
                .stream()
                .map(TeamResourceAdapter::toPokemonResponse)
                .toList();

        return new UserPokemonResponse(orm.id(), orm.userId(), team, capture);
    }

    private static PokemonResponse toPokemonResponse(Pokemon pokemon) {
        return new PokemonResponse(
                pokemon.index(),
                pokemon.name(),
                pokemon.image(),
                pokemon.types(),
                pokemon.abilities().stream()
                        .map(p -> new PowerPokemonResponse(p.name(), p.strength()))
                        .toList()
        );
    }
}
