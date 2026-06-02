package io.github.mrspock182.pokemon.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.repository.orm.CapturePokemonOrm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

@Configuration
public class DynamoDbCaptureConfig {

    @Bean
    public DynamoDbTable<CapturePokemonOrm> capturePokemonTable(
            ObjectMapper objectMapper,
            DynamoDbEnhancedClient enhancedClient,
            @Value("${dynamodb.tables.capture-pokemon}") String tableName) {
        ListPokemonConverter listPokemonConverter = new ListPokemonConverter(objectMapper);

        StaticTableSchema<CapturePokemonOrm> schema =
                StaticTableSchema.builder(CapturePokemonOrm.class)
                        .newItemSupplier(CapturePokemonOrm::new)
                        .addAttribute(String.class, a -> a.name("id")
                                .getter(CapturePokemonOrm::getId)
                                .setter(CapturePokemonOrm::setId)
                                .tags(StaticAttributeTags.primaryPartitionKey()))
                        .addAttribute(String.class, a -> a.name("userId")
                                .getter(CapturePokemonOrm::getUserId)
                                .setter(CapturePokemonOrm::setUserId)
                                .tags(StaticAttributeTags.secondaryPartitionKey("UserIdIndex")))
                        .addAttribute(EnhancedType.listOf(Pokemon.class), a -> a.name("team")
                                .getter(CapturePokemonOrm::getTeam)
                                .setter(CapturePokemonOrm::setTeam)
                                .attributeConverter(listPokemonConverter))
                        .addAttribute(EnhancedType.listOf(Pokemon.class), a -> a.name("options")
                                .getter(CapturePokemonOrm::getOptions)
                                .setter(CapturePokemonOrm::setOptions)
                                .attributeConverter(listPokemonConverter))
                        .build();

        return enhancedClient.table(tableName, schema);
    }
}
