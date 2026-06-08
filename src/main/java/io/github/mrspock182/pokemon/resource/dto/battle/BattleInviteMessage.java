package io.github.mrspock182.pokemon.resource.dto.battle;

public record BattleInviteMessage(
        String type,
        String battleId,
        String senderUsername
) {
    public BattleInviteMessage(String battleId, String senderUsername) {
        this("BATTLE_INVITE", battleId, senderUsername);
    }
}
