package io.github.mrspock182.pokemon.repository;

import io.github.mrspock182.pokemon.entity.User;
import io.github.mrspock182.pokemon.exception.BadRequestException;
import io.github.mrspock182.pokemon.exception.InternalServerError;
import io.github.mrspock182.pokemon.repository.orm.UserOrm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    private final DynamoDbTable<UserOrm> table;

    public UserRepository(DynamoDbTable<UserOrm> table) {
        this.table = table;
    }

    public User save(String username, String passwordHash) {
        try {
            findByUsername(username).ifPresent(u -> {
                throw new BadRequestException("Username já está em uso");
            });

            UserOrm orm = new UserOrm();
            orm.setId(UUID.randomUUID().toString());
            orm.setUsername(username);
            orm.setPasswordHash(passwordHash);
            orm.setLevel(1);
            orm.setVitorias(0);
            orm.setDerrotas(0);

            table.putItem(orm);
            return toUser(orm);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Erro ao salvar usuário: {}", username, ex);
            throw new InternalServerError("Erro ao salvar usuário", ex);
        }
    }

    public Optional<User> findById(String id) {
        try {
            UserOrm orm = table.getItem(Key.builder().partitionValue(id).build());
            return Optional.ofNullable(orm).map(this::toUser);
        } catch (Exception ex) {
            LOG.error("Erro ao buscar usuário por id: {}", id, ex);
            throw new InternalServerError("Erro ao buscar usuário", ex);
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            DynamoDbIndex<UserOrm> index = table.index("UsernameIndex");

            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(username).build());

            return index.query(queryConditional)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .findFirst()
                    .map(this::toUser);
        } catch (Exception ex) {
            LOG.error("Erro ao buscar usuário por username: {}", username, ex);
            throw new InternalServerError("Erro ao buscar usuário", ex);
        }
    }

    public User updateStats(
            String id,
            Integer level,
            Integer vitorias,
            Integer derrotas) {
        try {
            UserOrm orm = table.getItem(Key.builder().partitionValue(id).build());
            if (orm == null) {
                throw new BadRequestException("Usuário não encontrado");
            }
            orm.setLevel(level);
            orm.setVitorias(vitorias);
            orm.setDerrotas(derrotas);
            table.putItem(orm);
            return toUser(orm);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Erro ao atualizar stats do usuário: {}", id, ex);
            throw new InternalServerError("Erro ao atualizar stats do usuário", ex);
        }
    }

    private User toUser(UserOrm orm) {
        return new User(orm.getId(), orm.getUsername(), orm.getPasswordHash(),
                orm.getLevel(), orm.getVitorias(), orm.getDerrotas());
    }
}
