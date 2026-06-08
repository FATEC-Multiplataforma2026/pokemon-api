package io.github.mrspock182.pokemon.resource.dto.battle;

import java.math.BigDecimal;

public record RoundResultMessage(
        String type,
        int roundNumber,
        CharacterResult characterA,
        CharacterResult characterB,
        String selectedAttribute,
        String roundWinnerId,
        int winsA,
        int winsB
) {
    public RoundResultMessage(
            int roundNumber,
            CharacterResult characterA,
            CharacterResult characterB,
            String selectedAttribute,
            String roundWinnerId,
            int winsA,
            int winsB) {
        this("ROUND_RESULT", roundNumber, characterA, characterB, selectedAttribute, roundWinnerId, winsA, winsB);
    }

    public record CharacterResult(String name, BigDecimal attributeValue) {}
}
