package io.github.mrspock182.pokemon.resource.dto.battle;

public record BattleDeclinedMessage(String type, String battleId) {
    public BattleDeclinedMessage(String battleId) {
        this("BATTLE_DECLINED", battleId);
    }
}
