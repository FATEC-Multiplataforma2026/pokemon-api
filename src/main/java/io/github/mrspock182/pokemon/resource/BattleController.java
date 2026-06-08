package io.github.mrspock182.pokemon.resource;

import io.github.mrspock182.pokemon.exception.UnauthorizedException;
import io.github.mrspock182.pokemon.resource.dto.BattleInviteResponse;
import io.github.mrspock182.pokemon.service.BattleService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api-pokemon/battle/v1")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @ResponseStatus(OK)
    @PostMapping("/invite/{targetUsername}")
    public BattleInviteResponse invite(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String targetUsername) {
        return battleService.invite(extractUserId(authHeader), targetUsername);
    }

    @ResponseStatus(OK)
    @PostMapping("/accept/{battleId}")
    public void accept(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String battleId) {
        battleService.accept(extractUserId(authHeader), battleId);
    }

    @ResponseStatus(OK)
    @PostMapping("/decline/{battleId}")
    public void decline(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String battleId) {
        battleService.decline(extractUserId(authHeader), battleId);
    }

    @ResponseStatus(OK)
    @PostMapping("/{battleId}/round")
    public void playRound(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String battleId) {
        battleService.playRound(extractUserId(authHeader), battleId);
    }

    private String extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token ausente ou inválido");
        }
        String token = authHeader.substring(7).trim();
        if (token.isBlank()) {
            throw new UnauthorizedException("Token ausente ou inválido");
        }
        return token;
    }
}
