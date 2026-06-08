package io.github.mrspock182.pokemon.repository;

import io.github.mrspock182.pokemon.exception.InternalServerError;
import io.github.mrspock182.pokemon.repository.orm.WsConnectionOrm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

@Repository
public class ConnectionRepository {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionRepository.class);

    private final DynamoDbTable<WsConnectionOrm> table;

    public ConnectionRepository(DynamoDbTable<WsConnectionOrm> table) {
        this.table = table;
    }

    public void save(WsConnectionOrm connection) {
        try {
            table.putItem(connection);
        } catch (Exception ex) {
            LOG.error("Erro ao salvar conexão: {}", connection.getConnectionId(), ex);
            throw new InternalServerError("Erro ao salvar conexão", ex);
        }
    }

    public Optional<WsConnectionOrm> findByConnectionId(String connectionId) {
        try {
            WsConnectionOrm orm = table.getItem(Key.builder().partitionValue(connectionId).build());
            return Optional.ofNullable(orm);
        } catch (Exception ex) {
            LOG.error("Erro ao buscar conexão: {}", connectionId, ex);
            throw new InternalServerError("Erro ao buscar conexão", ex);
        }
    }

    public Optional<WsConnectionOrm> findByUserId(String userId) {
        try {
            DynamoDbIndex<WsConnectionOrm> index = table.index("UserIdIndex");
            QueryConditional condition = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(userId).build());
            return index.query(condition)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .findFirst();
        } catch (Exception ex) {
            LOG.error("Erro ao buscar conexão por userId: {}", userId, ex);
            throw new InternalServerError("Erro ao buscar conexão por userId", ex);
        }
    }

    public void updateBattleId(String connectionId, String battleId) {
        try {
            WsConnectionOrm orm = table.getItem(Key.builder().partitionValue(connectionId).build());
            if (orm != null) {
                orm.setBattleId(battleId);
                table.putItem(orm);
            }
        } catch (Exception ex) {
            LOG.error("Erro ao atualizar battleId da conexão: {}", connectionId, ex);
            throw new InternalServerError("Erro ao atualizar battleId da conexão", ex);
        }
    }

    public void delete(String connectionId) {
        try {
            table.deleteItem(Key.builder().partitionValue(connectionId).build());
        } catch (Exception ex) {
            LOG.error("Erro ao deletar conexão: {}", connectionId, ex);
            throw new InternalServerError("Erro ao deletar conexão", ex);
        }
    }
}
