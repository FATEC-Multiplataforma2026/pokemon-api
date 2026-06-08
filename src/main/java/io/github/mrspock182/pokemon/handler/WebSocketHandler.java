package io.github.mrspock182.pokemon.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import io.github.mrspock182.pokemon.PokemonApiApplication;
import io.github.mrspock182.pokemon.repository.ConnectionRepository;
import io.github.mrspock182.pokemon.repository.UserRepository;
import io.github.mrspock182.pokemon.repository.orm.WsConnectionOrm;
import io.github.mrspock182.pokemon.service.BattleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class WebSocketHandler
        implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);
    private static volatile ConfigurableApplicationContext springContext;

    @Override
    public APIGatewayV2WebSocketResponse handleRequest(APIGatewayV2WebSocketEvent event, Context context) {
        ConfigurableApplicationContext ctx = getSpringContext();
        String routeKey = event.getRequestContext().getRouteKey();
        String connectionId = event.getRequestContext().getConnectionId();

        LOG.info("WebSocket route={} connectionId={}", routeKey, connectionId);

        return switch (routeKey) {
            case "$connect" -> handleConnect(event, connectionId, ctx);
            case "$disconnect" -> handleDisconnect(connectionId, ctx);
            default -> ok();
        };
    }

    private APIGatewayV2WebSocketResponse handleConnect(
            APIGatewayV2WebSocketEvent event,
            String connectionId,
            ConfigurableApplicationContext ctx) {

        Map<String, String> params = event.getQueryStringParameters();
        String token = params != null ? params.get("token") : null;

        if (token == null || token.isBlank()) {
            LOG.warn("Conexão recusada: token ausente connectionId={}", connectionId);
            return unauthorized();
        }

        UserRepository userRepository = ctx.getBean(UserRepository.class);
        Optional<io.github.mrspock182.pokemon.entity.User> user = userRepository.findById(token);
        if (user.isEmpty()) {
            LOG.warn("Conexão recusada: usuário não encontrado connectionId={}", connectionId);
            return unauthorized();
        }

        ConnectionRepository connectionRepository = ctx.getBean(ConnectionRepository.class);
        WsConnectionOrm conn = new WsConnectionOrm();
        conn.setConnectionId(connectionId);
        conn.setUserId(token);
        conn.setTtl(Instant.now().plusSeconds(86400).getEpochSecond());
        connectionRepository.save(conn);

        LOG.info("Usuário {} conectado via connectionId={}", token, connectionId);
        return ok();
    }

    private APIGatewayV2WebSocketResponse handleDisconnect(
            String connectionId,
            ConfigurableApplicationContext ctx) {

        ConnectionRepository connectionRepository = ctx.getBean(ConnectionRepository.class);
        connectionRepository.findByConnectionId(connectionId).ifPresent(conn -> {
            if (conn.getBattleId() != null) {
                BattleService battleService = ctx.getBean(BattleService.class);
                battleService.handleDisconnect(conn);
            }
            connectionRepository.delete(connectionId);
            LOG.info("Usuário {} desconectado connectionId={}", conn.getUserId(), connectionId);
        });

        return ok();
    }

    private ConfigurableApplicationContext getSpringContext() {
        if (springContext == null) {
            synchronized (WebSocketHandler.class) {
                if (springContext == null) {
                    springContext = SpringApplication.run(PokemonApiApplication.class);
                }
            }
        }
        return springContext;
    }

    private APIGatewayV2WebSocketResponse ok() {
        APIGatewayV2WebSocketResponse response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        return response;
    }

    private APIGatewayV2WebSocketResponse unauthorized() {
        APIGatewayV2WebSocketResponse response = new APIGatewayV2WebSocketResponse();
        response.setStatusCode(401);
        return response;
    }
}
