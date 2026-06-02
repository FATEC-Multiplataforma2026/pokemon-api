package io.github.mrspock182.pokemon.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrspock182.pokemon.entity.Pokemon;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.List;

public class ListPokemonConverter implements AttributeConverter<List<Pokemon>> {

    private final ObjectMapper objectMapper;

    public ListPokemonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AttributeValue transformFrom(List<Pokemon> input) {
        if (input == null) {
            return AttributeValue.builder().nul(true).build();
        }
        try {
            return AttributeValue.builder().s(objectMapper.writeValueAsString(input)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar lista de Pokemon", e);
        }
    }

    @Override
    public List<Pokemon> transformTo(AttributeValue input) {
        if (input == null || input.s() == null || input.s().isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(input.s(), new TypeReference<List<Pokemon>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao desserializar lista de Pokemon", e);
        }
    }

    @Override
    public EnhancedType<List<Pokemon>> type() {
        return EnhancedType.listOf(Pokemon.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}