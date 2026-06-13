package io.github.mrspock182.pokemon.repository;

import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.UserPokemon;
import io.github.mrspock182.pokemon.exception.BadRequestException;
import io.github.mrspock182.pokemon.exception.InternalServerError;
import io.github.mrspock182.pokemon.exception.NotFoundException;
import io.github.mrspock182.pokemon.repository.adapter.TeamRepositoryAdapter;
import io.github.mrspock182.pokemon.repository.orm.CapturePokemonOrm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class TeamRepository {
    private static final Logger LOG = LoggerFactory.getLogger(TeamRepository.class);

    private final DynamoDbTable<CapturePokemonOrm> table;

    public TeamRepository(DynamoDbTable<CapturePokemonOrm> table) {
        this.table = table;
    }

    public UserPokemon save(String userId, List<Pokemon> team) {
        try {
            CapturePokemonOrm orm = new CapturePokemonOrm();
            orm.setId(UUID.randomUUID().toString());
            orm.setUserId(userId);
            orm.setTeam(team);
            orm.setOptions(null);

            table.putItem(orm);
            return TeamRepositoryAdapter.cast(orm);
        } catch (Exception ex) {
            LOG.error("Erro ao salvar time do usuario: {}", userId, ex);
            throw new InternalServerError("Erro ao salvar time do usuario", ex);
        }
    }

    public UserPokemon reorder(String userId, List<String> teamOrder) {
        try {
            UserPokemon userPokemon = findByUserId(userId);
            List<String> currentIndices = userPokemon.team()
                    .stream()
                    .map(Pokemon::index)
                    .toList();

            if (teamOrder.size() != currentIndices.size()) {
                throw new BadRequestException("A lista de ordem deve conter exatamente "
                        + currentIndices.size() + " pokémons");
            }

            boolean allMatch = teamOrder.stream()
                    .allMatch(currentIndices::contains)
                    && currentIndices.stream()
                    .allMatch(teamOrder::contains);

            if (!allMatch) {
                throw new BadRequestException("A lista de ordem deve conter os mesmos pokémons do time atual");
            }

            List<Pokemon> reorderedTeam = teamOrder.stream()
                    .map(index -> userPokemon.team().stream()
                            .filter(p -> p.index().equals(index))
                            .findFirst()
                            .orElseThrow())
                    .toList();

            CapturePokemonOrm orm = new CapturePokemonOrm();
            orm.setId(userPokemon.id());
            orm.setUserId(userPokemon.userId());
            orm.setTeam(reorderedTeam);
            orm.setOptions(userPokemon.capture());
            table.updateItem(orm);

            return TeamRepositoryAdapter.cast(orm);
        } catch (NotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Erro ao reordenar time do usuário: {}", userId, ex);
            throw new InternalServerError("Erro ao reordenar time", ex);
        }
    }

    public UserPokemon update(String userId, String removedPokemon, String newPokemon) {
        try {
            UserPokemon userPokemon = findByUserId(userId);

            Pokemon pokemonToAdd = userPokemon.capture()
                    .stream()
                    .filter(p -> p.index().equals(newPokemon))
                    .findFirst()
                    .orElseThrow(() ->
                            new NotFoundException("Pokemon não encontrado nos capturados"));

            Pokemon pokemonToRemove = userPokemon.team()
                    .stream()
                    .filter(p -> p.index().equals(removedPokemon))
                    .findFirst()
                    .orElseThrow(() ->
                            new NotFoundException("Pokemon não encontrado no time"));

            boolean newAlreadyInTeam = userPokemon.team()
                    .stream().anyMatch(p -> p.index().equals(pokemonToAdd.index()));

            boolean removedAlreadyInCapture = userPokemon.capture()
                    .stream().anyMatch(p -> p.index().equals(pokemonToRemove.index()));

            if (newAlreadyInTeam) {
                throw new BadRequestException("Pokemon já existe no time: " + pokemonToAdd.index());
            }

            if (removedAlreadyInCapture) {
                throw new BadRequestException("Pokemon já existe nos capturados: " + pokemonToRemove.index());
            }

            List<Pokemon> updatedTeam = Stream.concat(
                    userPokemon.team()
                            .stream()
                            .filter(p -> !p.index().equals(removedPokemon)),
                    Stream.of(pokemonToAdd)).toList();

            List<Pokemon> updatedCapture = Stream.concat(
                    userPokemon.capture()
                            .stream()
                            .filter(p -> !p.index().equals(newPokemon)),
                    Stream.of(pokemonToRemove)).toList();

            CapturePokemonOrm orm = new CapturePokemonOrm();
            orm.setId(userPokemon.id());
            orm.setUserId(userPokemon.userId());
            orm.setTeam(updatedTeam);
            orm.setOptions(updatedCapture);
            table.updateItem(orm);

            return TeamRepositoryAdapter.cast(orm);
        } catch (NotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Erro ao atualizar pokemon ", ex);
            throw new InternalServerError("Erro atualizar pokemon", ex);
        }
    }

    public UserPokemon addCapture(String userId, Pokemon pokemon) {
        try {
            UserPokemon userPokemon = findByUserId(userId);

            boolean alreadyInTeam = userPokemon.team() != null && userPokemon.team()
                    .stream().anyMatch(p -> p.index().equals(pokemon.index()));
            boolean alreadyInCapture = userPokemon.capture() != null && userPokemon.capture()
                    .stream().anyMatch(p -> p.index().equals(pokemon.index()));
            if (alreadyInTeam || alreadyInCapture) {
                throw new BadRequestException("Pokemon já existe no time ou nos capturados: " + pokemon.index());
            }

            List<Pokemon> updatedCapture = new ArrayList<>(
                    userPokemon.capture() != null ? userPokemon.capture() : List.of());
            updatedCapture.add(pokemon);

            CapturePokemonOrm orm = new CapturePokemonOrm();
            orm.setId(userPokemon.id());
            orm.setUserId(userPokemon.userId());
            orm.setTeam(userPokemon.team());
            orm.setOptions(updatedCapture);
            table.updateItem(orm);

            return TeamRepositoryAdapter.cast(orm);
        } catch (NotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Erro ao adicionar pokemon capturado para usuário: {}", userId, ex);
            throw new InternalServerError("Erro ao adicionar pokemon capturado", ex);
        }
    }

    public UserPokemon removeCapture(String userId, String index) {
        try {
            UserPokemon userPokemon = findByUserId(userId);
            List<Pokemon> updatedCapture = new ArrayList<>(
                    userPokemon.capture() != null ? userPokemon.capture() : List.of());
            boolean removed = updatedCapture.removeIf(p -> p.index().equals(index));
            if (!removed) {
                throw new NotFoundException("Pokemon não encontrado no índice informado: " + index);
            }

            CapturePokemonOrm orm = new CapturePokemonOrm();
            orm.setId(userPokemon.id());
            orm.setUserId(userPokemon.userId());
            orm.setTeam(userPokemon.team());
            orm.setOptions(updatedCapture);
            table.updateItem(orm);

            return TeamRepositoryAdapter.cast(orm);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Erro ao remover pokemon capturado para usuário: {}", userId, ex);
            throw new InternalServerError("Erro ao remover pokemon capturado", ex);
        }
    }

    public UserPokemon findByUserId(String userId) {
        try {
            DynamoDbIndex<CapturePokemonOrm> userIndex = table.index("UserIdIndex");

            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(userId).build());

            CapturePokemonOrm pokemonOrm = userIndex.query(queryConditional)
                    .stream()
                    .flatMap(page -> page.items().stream())
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

            return TeamRepositoryAdapter.cast(pokemonOrm);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.error("Erro na consulta do pokemon team: {}", userId, ex);
            throw new InternalServerError("Erro consulta pokemon team", ex);
        }
    }
}