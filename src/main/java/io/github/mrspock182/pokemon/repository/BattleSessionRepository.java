package io.github.mrspock182.pokemon.repository;

import io.github.mrspock182.pokemon.exception.InternalServerError;
import io.github.mrspock182.pokemon.repository.orm.BattleSessionOrm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
public class BattleSessionRepository {
    private static final Logger LOG = LoggerFactory.getLogger(BattleSessionRepository.class);

    private final DynamoDbTable<BattleSessionOrm> table;

    public BattleSessionRepository(DynamoDbTable<BattleSessionOrm> table) {
        this.table = table;
    }

    public void save(BattleSessionOrm session) {
        try {
            table.putItem(session);
        } catch (Exception ex) {
            LOG.error("Erro ao salvar sessão de batalha: {}", session.getBattleId(), ex);
            throw new InternalServerError("Erro ao salvar sessão de batalha", ex);
        }
    }

    public Optional<BattleSessionOrm> findById(String battleId) {
        try {
            BattleSessionOrm orm = table.getItem(Key.builder().partitionValue(battleId).build());
            return Optional.ofNullable(orm);
        } catch (Exception ex) {
            LOG.error("Erro ao buscar sessão de batalha: {}", battleId, ex);
            throw new InternalServerError("Erro ao buscar sessão de batalha", ex);
        }
    }

    public void delete(String battleId) {
        try {
            table.deleteItem(Key.builder().partitionValue(battleId).build());
        } catch (Exception ex) {
            LOG.error("Erro ao deletar sessão de batalha: {}", battleId, ex);
            throw new InternalServerError("Erro ao deletar sessão de batalha", ex);
        }
    }
}
