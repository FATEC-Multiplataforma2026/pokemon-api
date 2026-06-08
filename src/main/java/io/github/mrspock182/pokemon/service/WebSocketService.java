package io.github.mrspock182.pokemon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrspock182.pokemon.repository.ConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

@Service
public class WebSocketService {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketService.class);

    private final ApiGatewayManagementApiClient wsClient;
    private final ConnectionRepository connectionRepository;
    private final ObjectMapper objectMapper;

    public WebSocketService(
            ApiGatewayManagementApiClient wsClient,
            ConnectionRepository connectionRepository,
            ObjectMapper objectMapper) {
        this.wsClient = wsClient;
        this.connectionRepository = connectionRepository;
        this.objectMapper = objectMapper;
    }

    public void sendToConnection(String connectionId, Object payload) {
        if (connectionId == null || connectionId.isBlank()) {
            return;
        }
        try {
            byte[] data = objectMapper.writeValueAsBytes(payload);
            wsClient.postToConnection(PostToConnectionRequest.builder()
                    .connectionId(connectionId)
                    .data(SdkBytes.fromByteArray(data))
                    .build());
        } catch (GoneException e) {
            LOG.warn("Conexão {} não existe mais, removendo", connectionId);
            connectionRepository.delete(connectionId);
        } catch (Exception e) {
            LOG.error("Erro ao enviar mensagem para conexão {}", connectionId, e);
        }
    }
}
