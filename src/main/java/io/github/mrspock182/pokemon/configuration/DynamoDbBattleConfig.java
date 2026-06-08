package io.github.mrspock182.pokemon.configuration;

import io.github.mrspock182.pokemon.repository.orm.BattleSessionOrm;
import io.github.mrspock182.pokemon.repository.orm.WsConnectionOrm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

@Configuration
public class DynamoDbBattleConfig {

    @Bean
    public DynamoDbTable<WsConnectionOrm> wsConnectionTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${dynamodb.tables.connections}") String tableName) {

        StaticTableSchema<WsConnectionOrm> schema = StaticTableSchema.builder(WsConnectionOrm.class)
                .newItemSupplier(WsConnectionOrm::new)
                .addAttribute(String.class, a -> a.name("connectionId")
                        .getter(WsConnectionOrm::getConnectionId)
                        .setter(WsConnectionOrm::setConnectionId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("userId")
                        .getter(WsConnectionOrm::getUserId)
                        .setter(WsConnectionOrm::setUserId)
                        .tags(StaticAttributeTags.secondaryPartitionKey("UserIdIndex")))
                .addAttribute(String.class, a -> a.name("battleId")
                        .getter(WsConnectionOrm::getBattleId)
                        .setter(WsConnectionOrm::setBattleId))
                .addAttribute(Long.class, a -> a.name("ttl")
                        .getter(WsConnectionOrm::getTtl)
                        .setter(WsConnectionOrm::setTtl))
                .build();

        return enhancedClient.table(tableName, schema);
    }

    @Bean
    public DynamoDbTable<BattleSessionOrm> battleSessionTable(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${dynamodb.tables.battles}") String tableName) {

        StaticTableSchema<BattleSessionOrm> schema = StaticTableSchema.builder(BattleSessionOrm.class)
                .newItemSupplier(BattleSessionOrm::new)
                .addAttribute(String.class, a -> a.name("battleId")
                        .getter(BattleSessionOrm::getBattleId)
                        .setter(BattleSessionOrm::setBattleId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("playerAId")
                        .getter(BattleSessionOrm::getPlayerAId)
                        .setter(BattleSessionOrm::setPlayerAId))
                .addAttribute(String.class, a -> a.name("playerBId")
                        .getter(BattleSessionOrm::getPlayerBId)
                        .setter(BattleSessionOrm::setPlayerBId))
                .addAttribute(String.class, a -> a.name("connectionIdA")
                        .getter(BattleSessionOrm::getConnectionIdA)
                        .setter(BattleSessionOrm::setConnectionIdA))
                .addAttribute(String.class, a -> a.name("connectionIdB")
                        .getter(BattleSessionOrm::getConnectionIdB)
                        .setter(BattleSessionOrm::setConnectionIdB))
                .addAttribute(String.class, a -> a.name("status")
                        .getter(BattleSessionOrm::getStatus)
                        .setter(BattleSessionOrm::setStatus))
                .addAttribute(String.class, a -> a.name("teamA")
                        .getter(BattleSessionOrm::getTeamA)
                        .setter(BattleSessionOrm::setTeamA))
                .addAttribute(String.class, a -> a.name("teamB")
                        .getter(BattleSessionOrm::getTeamB)
                        .setter(BattleSessionOrm::setTeamB))
                .addAttribute(Integer.class, a -> a.name("winsA")
                        .getter(BattleSessionOrm::getWinsA)
                        .setter(BattleSessionOrm::setWinsA))
                .addAttribute(Integer.class, a -> a.name("winsB")
                        .getter(BattleSessionOrm::getWinsB)
                        .setter(BattleSessionOrm::setWinsB))
                .addAttribute(Integer.class, a -> a.name("currentRound")
                        .getter(BattleSessionOrm::getCurrentRound)
                        .setter(BattleSessionOrm::setCurrentRound))
                .addAttribute(String.class, a -> a.name("winnerId")
                        .getter(BattleSessionOrm::getWinnerId)
                        .setter(BattleSessionOrm::setWinnerId))
                .addAttribute(Long.class, a -> a.name("ttl")
                        .getter(BattleSessionOrm::getTtl)
                        .setter(BattleSessionOrm::setTtl))
                .build();

        return enhancedClient.table(tableName, schema);
    }
}
