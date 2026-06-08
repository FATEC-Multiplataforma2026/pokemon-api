package io.github.mrspock182.pokemon.resource.dto.battle;

public record BattleAbandonedMessage(String type, String battleId) {
    public BattleAbandonedMessage(String battleId) {
        this("BATTLE_ABANDONED", battleId);
    }
}
