package io.github.mrspock182.pokemon.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;

import java.net.URI;

@Configuration
public class WebSocketClientConfig {

    @Bean
    public ApiGatewayManagementApiClient apiGatewayManagementApiClient(
            @Value("${websocket.endpoint}") String endpoint,
            @Value("${dynamodb.region}") String region,
            @Value("${dynamodb.access-key}") String accessKey,
            @Value("${dynamodb.secret-key}") String secretKey) {

        return ApiGatewayManagementApiClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
