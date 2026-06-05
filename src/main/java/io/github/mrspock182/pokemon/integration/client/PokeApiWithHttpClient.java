package io.github.mrspock182.pokemon.integration.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrspock182.pokemon.integration.dto.PokemonDetailResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class PokeApiWithHttpClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PokeApiWithHttpClient(
            ObjectMapper objectMapper,
            @Value("${pokeapi.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public PokemonListResponse listPokemons(int limit) {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/pokemon")
                    .queryParam("limit", limit)
                    .toUriString();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validarResposta(response);
            return objectMapper.readValue(response.body(), PokemonListResponse.class);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao listar pokemons via HttpClient", e);
        }
    }

    public PokemonDetailResponse getPokemon(int id) {
        try {
            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/pokemon/{id}")
                    .buildAndExpand(id)
                    .toUriString();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validarResposta(response);
            return objectMapper.readValue(response.body(), PokemonDetailResponse.class);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao buscar detalhes do pokemon " + id, e);
        }
    }

    private void validarResposta(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            throw new RuntimeException(
                    "Erro na API externa. Status Code: " + response.statusCode()
                            + " - " + response.body());
        }
    }

}