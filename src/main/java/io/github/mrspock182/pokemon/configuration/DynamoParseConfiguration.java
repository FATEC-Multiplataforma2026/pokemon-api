package io.github.mrspock182.pokemon.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DynamoParseConfiguration {
    private DynamoParseConfiguration() {
    }

    public static Map<String, AttributeValue> decode(String page) {
        if (page == null || page.isBlank()) {
            return null;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(page);
            String json = new String(decoded, StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> simpleMap =
                    mapper.readValue(json, new TypeReference<>() {
                    });

            Map<String, AttributeValue> result = new HashMap<>();

            simpleMap.forEach((key, value) -> {
                String[] parts = value.split("\\|", 2);
                String type = parts[0];
                String val = parts[1];

                switch (type) {
                    case "S" -> result.put(key, AttributeValue.builder().s(val).build());
                    case "N" -> result.put(key, AttributeValue.builder().n(val).build());
                }
            });

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Page inválida", e);
        }
    }

    public static String encode(Map<String, AttributeValue> lastEvaluatedKey) {
        if (lastEvaluatedKey == null || lastEvaluatedKey.isEmpty()) {
            return null;
        }

        Map<String, String> simpleMap = new HashMap<>();
        lastEvaluatedKey.forEach((key, value) -> {
            if (value.s() != null) {
                simpleMap.put(key, "S|" + value.s());
            } else if (value.n() != null) {
                simpleMap.put(key, "N|" + value.n());
            }
        });

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(simpleMap);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar lastEvaluatedKey", e);
        }
    }
}