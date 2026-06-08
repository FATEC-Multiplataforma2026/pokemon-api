package io.github.mrspock182.pokemon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.Power;
import io.github.mrspock182.pokemon.entity.User;
import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.exception.BadRequestException;
import io.github.mrspock182.pokemon.exception.ConflictException;
import io.github.mrspock182.pokemon.exception.InternalServerError;
import io.github.mrspock182.pokemon.exception.NotFoundException;
import io.github.mrspock182.pokemon.exception.UnauthorizedException;
import io.github.mrspock182.pokemon.integration.PokemonIntegration;
import io.github.mrspock182.pokemon.repository.BattleSessionRepository;
import io.github.mrspock182.pokemon.repository.ConnectionRepository;
import io.github.mrspock182.pokemon.repository.TeamRepository;
import io.github.mrspock182.pokemon.repository.UserRepository;
import io.github.mrspock182.pokemon.repository.orm.BattleSessionOrm;
import io.github.mrspock182.pokemon.repository.orm.WsConnectionOrm;
import io.github.mrspock182.pokemon.resource.dto.BattleInviteResponse;
import io.github.mrspock182.pokemon.resource.dto.battle.BattleAbandonedMessage;
import io.github.mrspock182.pokemon.resource.dto.battle.BattleDeclinedMessage;
import io.github.mrspock182.pokemon.resource.dto.battle.BattleFinishedMessage;
import io.github.mrspock182.pokemon.resource.dto.battle.BattleInviteMessage;
import io.github.mrspock182.pokemon.resource.dto.battle.BattleStartedMessage;
import io.github.mrspock182.pokemon.resource.dto.battle.RoundResultMessage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BattleService {

    private static final long INVITE_TTL_SECONDS = 30;
    private static final long BATTLE_TTL_SECONDS = 7200;
    private static final int WINS_TO_WIN = 3;
    private static final int TOTAL_POKEMONS = 151;

    private final BattleSessionRepository battleSessionRepository;
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PokemonIntegration pokemonIntegration;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    public BattleService(
            BattleSessionRepository battleSessionRepository,
            ConnectionRepository connectionRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            PokemonIntegration pokemonIntegration,
            WebSocketService webSocketService,
            ObjectMapper objectMapper) {
        this.battleSessionRepository = battleSessionRepository;
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.pokemonIntegration = pokemonIntegration;
        this.webSocketService = webSocketService;
        this.objectMapper = objectMapper;
    }

    public BattleInviteResponse invite(String playerAId, String targetUsername) {
        User playerA = userRepository.findById(playerAId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        User playerB = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + targetUsername));

        if (playerA.id().equals(playerB.id())) {
            throw new BadRequestException("Não é possível batalhar contra si mesmo");
        }

        WsConnectionOrm connA = connectionRepository.findByUserId(playerAId)
                .orElseThrow(() -> new ConflictException("Você precisa estar conectado via WebSocket"));

        WsConnectionOrm connB = connectionRepository.findByUserId(playerB.id())
                .orElseThrow(() -> new ConflictException("Usuário offline"));

        String battleId = UUID.randomUUID().toString();

        BattleSessionOrm session = new BattleSessionOrm();
        session.setBattleId(battleId);
        session.setPlayerAId(playerAId);
        session.setPlayerBId(playerB.id());
        session.setConnectionIdA(connA.getConnectionId());
        session.setConnectionIdB(connB.getConnectionId());
        session.setStatus("PENDING");
        session.setWinsA(0);
        session.setWinsB(0);
        session.setCurrentRound(0);
        session.setTtl(Instant.now().plusSeconds(INVITE_TTL_SECONDS).getEpochSecond());

        battleSessionRepository.save(session);
        connectionRepository.updateBattleId(connA.getConnectionId(), battleId);

        webSocketService.sendToConnection(
                connB.getConnectionId(),
                new BattleInviteMessage(battleId, playerA.username()));

        return new BattleInviteResponse(battleId);
    }

    public void accept(String playerBId, String battleId) {
        BattleSessionOrm session = findValidSession(battleId);

        if (!session.getPlayerBId().equals(playerBId)) {
            throw new UnauthorizedException("Você não é o convidado desta batalha");
        }
        if (!"PENDING".equals(session.getStatus())) {
            throw new BadRequestException("Batalha não está com status PENDING");
        }
        if (session.getTtl() < Instant.now().getEpochSecond()) {
            throw new BadRequestException("Convite expirado");
        }

        UserPokemon userPokemonA = teamRepository.findByUserId(session.getPlayerAId());
        UserPokemon userPokemonB = teamRepository.findByUserId(playerBId);

        List<Pokemon> teamA = userPokemonA.team();
        List<Pokemon> teamB = userPokemonB.team();

        WsConnectionOrm connA = connectionRepository.findByUserId(session.getPlayerAId())
                .orElseThrow(() -> new ConflictException("Jogador A está offline"));
        WsConnectionOrm connB = connectionRepository.findByUserId(playerBId)
                .orElseThrow(() -> new ConflictException("Você precisa estar conectado via WebSocket"));

        session.setStatus("IN_PROGRESS");
        session.setTeamA(serializeTeam(teamA));
        session.setTeamB(serializeTeam(teamB));
        session.setConnectionIdA(connA.getConnectionId());
        session.setConnectionIdB(connB.getConnectionId());
        session.setTtl(Instant.now().plusSeconds(BATTLE_TTL_SECONDS).getEpochSecond());

        battleSessionRepository.save(session);
        connectionRepository.updateBattleId(connB.getConnectionId(), battleId);

        webSocketService.sendToConnection(
                connA.getConnectionId(),
                new BattleStartedMessage(battleId, teamA, teamB));

        webSocketService.sendToConnection(
                connB.getConnectionId(),
                new BattleStartedMessage(battleId, teamB, teamA));
    }

    public void decline(String playerBId, String battleId) {
        BattleSessionOrm session = findValidSession(battleId);

        if (!session.getPlayerBId().equals(playerBId)) {
            throw new UnauthorizedException("Você não é o convidado desta batalha");
        }

        battleSessionRepository.delete(battleId);

        webSocketService.sendToConnection(
                session.getConnectionIdA(),
                new BattleDeclinedMessage(battleId));
    }

    public void playRound(String userId, String battleId) {
        BattleSessionOrm session = findValidSession(battleId);

        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new BadRequestException("Batalha não está em andamento");
        }
        if (!session.getPlayerAId().equals(userId) && !session.getPlayerBId().equals(userId)) {
            throw new UnauthorizedException("Você não é participante desta batalha");
        }
        if (session.getTtl() < Instant.now().getEpochSecond()) {
            throw new BadRequestException("Sessão de batalha expirada");
        }

        int round = session.getCurrentRound();
        List<Pokemon> teamA = deserializeTeam(session.getTeamA());
        List<Pokemon> teamB = deserializeTeam(session.getTeamB());

        Pokemon charA = teamA.get(round);
        Pokemon charB = teamB.get(round);

        int abilityCount = Math.min(charA.abilities().size(), charB.abilities().size());
        int abilityIndex = ThreadLocalRandom.current().nextInt(abilityCount);

        Power powerA = charA.abilities().get(abilityIndex);
        Power powerB = charB.abilities().get(abilityIndex);

        BigDecimal valueA = powerA.strength();
        BigDecimal valueB = powerB.strength();
        String selectedAttribute = powerA.name();

        String roundWinnerId = valueA.compareTo(valueB) >= 0
                ? session.getPlayerAId()
                : session.getPlayerBId();

        if (roundWinnerId.equals(session.getPlayerAId())) {
            session.setWinsA(session.getWinsA() + 1);
        } else {
            session.setWinsB(session.getWinsB() + 1);
        }
        session.setCurrentRound(round + 1);

        battleSessionRepository.save(session);

        RoundResultMessage result = new RoundResultMessage(
                round + 1,
                new RoundResultMessage.CharacterResult(charA.name(), valueA),
                new RoundResultMessage.CharacterResult(charB.name(), valueB),
                selectedAttribute,
                roundWinnerId,
                session.getWinsA(),
                session.getWinsB());

        webSocketService.sendToConnection(session.getConnectionIdA(), result);
        webSocketService.sendToConnection(session.getConnectionIdB(), result);

        if (session.getWinsA() >= WINS_TO_WIN || session.getWinsB() >= WINS_TO_WIN) {
            finalizeBattle(session);
        }
    }

    public void handleDisconnect(WsConnectionOrm conn) {
        if (conn.getBattleId() == null) {
            return;
        }
        battleSessionRepository.findById(conn.getBattleId()).ifPresent(session -> {
            if ("FINISHED".equals(session.getStatus())) {
                return;
            }
            session.setStatus("FINISHED");
            battleSessionRepository.save(session);

            String opponentConnectionId = conn.getConnectionId().equals(session.getConnectionIdA())
                    ? session.getConnectionIdB()
                    : session.getConnectionIdA();

            webSocketService.sendToConnection(
                    opponentConnectionId,
                    new BattleAbandonedMessage(session.getBattleId()));
        });
    }

    private void finalizeBattle(BattleSessionOrm session) {
        String winnerId = session.getWinsA() >= WINS_TO_WIN
                ? session.getPlayerAId()
                : session.getPlayerBId();
        String loserId = winnerId.equals(session.getPlayerAId())
                ? session.getPlayerBId()
                : session.getPlayerAId();

        session.setStatus("FINISHED");
        session.setWinnerId(winnerId);
        battleSessionRepository.save(session);

        int randomPokemonId = ThreadLocalRandom.current().nextInt(1, TOTAL_POKEMONS + 1);
        Pokemon newPokemon = pokemonIntegration.findPokemonById(randomPokemonId);

        userRepository.findById(winnerId).ifPresent(winner ->
                userRepository.updateStats(
                        winner.id(),
                        winner.level() + 1,
                        winner.vitorias() + 1,
                        winner.derrotas()));

        userRepository.findById(loserId).ifPresent(loser ->
                userRepository.updateStats(
                        loser.id(),
                        loser.level(),
                        loser.vitorias(),
                        loser.derrotas() + 1));

        teamRepository.addCapture(winnerId, newPokemon);

        BattleFinishedMessage msg = new BattleFinishedMessage(winnerId, loserId, newPokemon);
        webSocketService.sendToConnection(session.getConnectionIdA(), msg);
        webSocketService.sendToConnection(session.getConnectionIdB(), msg);
    }

    private BattleSessionOrm findValidSession(String battleId) {
        return battleSessionRepository.findById(battleId)
                .orElseThrow(() -> new NotFoundException("Batalha não encontrada: " + battleId));
    }

    private String serializeTeam(List<Pokemon> team) {
        try {
            return objectMapper.writeValueAsString(team);
        } catch (Exception ex) {
            throw new InternalServerError("Erro ao serializar time", ex);
        }
    }

    private List<Pokemon> deserializeTeam(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Pokemon>>() {});
        } catch (Exception ex) {
            throw new InternalServerError("Erro ao desserializar time", ex);
        }
    }
}
