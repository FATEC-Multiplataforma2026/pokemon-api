package io.github.mrspock182.pokemon.configuration;

import io.github.mrspock182.pokemon.repository.orm.UserOrm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

@Configuration
public class DynamoDbUserConfig {

    @Bean
    public DynamoDbTable<UserOrm> userTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${dynamodb.tables.users}") String tableName) {

        StaticTableSchema<UserOrm> schema = StaticTableSchema.builder(UserOrm.class)
                .newItemSupplier(UserOrm::new)
                .addAttribute(String.class, a -> a.name("id")
                        .getter(UserOrm::getId)
                        .setter(UserOrm::setId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("username")
                        .getter(UserOrm::getUsername)
                        .setter(UserOrm::setUsername)
                        .tags(StaticAttributeTags.secondaryPartitionKey("UsernameIndex")))
                .addAttribute(String.class, a -> a.name("passwordHash")
                        .getter(UserOrm::getPasswordHash)
                        .setter(UserOrm::setPasswordHash))
                .addAttribute(Integer.class, a -> a.name("level")
                        .getter(UserOrm::getLevel)
                        .setter(UserOrm::setLevel))
                .addAttribute(Integer.class, a -> a.name("vitorias")
                        .getter(UserOrm::getVitorias)
                        .setter(UserOrm::setVitorias))
                .addAttribute(Integer.class, a -> a.name("derrotas")
                        .getter(UserOrm::getDerrotas)
                        .setter(UserOrm::setDerrotas))
                .build();

        return enhancedClient.table(tableName, schema);
    }
}
