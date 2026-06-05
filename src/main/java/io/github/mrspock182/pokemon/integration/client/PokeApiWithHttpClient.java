package io.github.mrspock182.pokemon.integration.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrspock182.pokemon.integration.dto.PokemonDetailResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonSpritesResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonStatInfoResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonStatSlotResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonTypeInfoResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonTypeSlotResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonItemResponse;
import io.github.mrspock182.pokemon.integration.dto.PokemonListResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class PokeApiWithHttpClient {
    private static final Logger log = LogManager.getLogger(PokeApiWithHttpClient.class);

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
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            validarResposta(response);
            return parsePokemonList(objectMapper.readTree(response.body()));
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
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            log.info("Response from Pokemon API: {}", response.body());

            return parsePokemonDetail(objectMapper.readTree(response.body()));

        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao buscar detalhes do pokemon-id: " +
                    id + " - message: " + ex.getMessage());
        }
    }

    private PokemonListResponse parsePokemonList(JsonNode root) {
        List<PokemonItemResponse> items = new ArrayList<>();
        for (JsonNode node : root.path("results")) {
            PokemonItemResponse item = new PokemonItemResponse();
            item.setName(node.path("name").asText());
            item.setUrl(node.path("url").asText());
            items.add(item);
        }
        PokemonListResponse list = new PokemonListResponse();
        list.setResults(items);
        return list;
    }

    private PokemonDetailResponse parsePokemonDetail(JsonNode root) {
        PokemonDetailResponse detail = new PokemonDetailResponse();
        detail.setId(root.get("id").asInt());
        detail.setName(root.get("name").asText());

        PokemonSpritesResponse sprites = new PokemonSpritesResponse();
        sprites.setFrontDefault(root.path("sprites").path("front_default").asText(null));
        detail.setSprites(sprites);

        List<PokemonTypeSlotResponse> types = new ArrayList<>();
        for (JsonNode slot : root.path("types")) {
            PokemonTypeInfoResponse typeInfo = new PokemonTypeInfoResponse();
            typeInfo.setName(slot.path("type").path("name").asText());
            PokemonTypeSlotResponse typeSlot = new PokemonTypeSlotResponse();
            typeSlot.setType(typeInfo);
            types.add(typeSlot);
        }
        detail.setTypes(types);

        List<PokemonStatSlotResponse> stats = new ArrayList<>();
        for (JsonNode slot : root.path("stats")) {
            PokemonStatInfoResponse statInfo = new PokemonStatInfoResponse();
            statInfo.setName(slot.path("stat").path("name").asText());
            PokemonStatSlotResponse statSlot = new PokemonStatSlotResponse();
            statSlot.setBaseStat(slot.path("base_stat").asInt());
            statSlot.setStat(statInfo);
            stats.add(statSlot);
        }
        detail.setStats(stats);

        return detail;
    }

    private void validarResposta(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Erro na API externa. Status Code: "
                    + response.statusCode()
                    + " - " + response.body());
        }
    }

}