package io.github.mrspock182.pokemon.resource.dto.battle;

import io.github.mrspock182.pokemon.entity.Pokemon;

public record BattleFinishedMessage(
        String type,
        String winnerId,
        String loserId,
        Pokemon newCharacter
) {
    public BattleFinishedMessage(String winnerId, String loserId, Pokemon newCharacter) {
        this("BATTLE_FINISHED", winnerId, loserId, newCharacter);
    }
}
