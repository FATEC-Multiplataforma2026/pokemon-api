package io.github.mrspock182.pokemon.resource.dto.battle;

import io.github.mrspock182.pokemon.entity.Pokemon;

import java.util.List;

public record BattleStartedMessage(
        String type,
        String battleId,
        List<Pokemon> myTeam,
        List<Pokemon> opponentTeam
) {
    public BattleStartedMessage(String battleId, List<Pokemon> myTeam, List<Pokemon> opponentTeam) {
        this("BATTLE_STARTED", battleId, myTeam, opponentTeam);
    }
}
