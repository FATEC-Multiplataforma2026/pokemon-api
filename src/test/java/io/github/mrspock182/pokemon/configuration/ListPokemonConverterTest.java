package io.github.mrspock182.pokemon.configuration;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.mrspock182.pokemon.entity.Pokemon;
import io.github.mrspock182.pokemon.entity.Power;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListPokemonConverterTest {

    private ListPokemonConverter converter;

    @BeforeEach
    void setUp() {
        var objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        converter = new ListPokemonConverter(objectMapper);
    }

    @Test
    void shouldSerializeAndDeserializePokemonListRoundtrip() {
        List<Pokemon> team = List.of(
                new Pokemon("1", "bulbasaur", "https://pokeapi.co/img/1.png",
                        List.of("grass", "poison"),
                        List.of(new Power("hp", BigDecimal.valueOf(45)),
                                new Power("attack", BigDecimal.valueOf(49)))),
                new Pokemon("4", "charmander", "https://pokeapi.co/img/4.png",
                        List.of("fire"),
                        List.of(new Power("hp", BigDecimal.valueOf(39)),
                                new Power("speed", BigDecimal.valueOf(65))))
        );

        AttributeValue serialized = converter.transformFrom(team);

        assertThat(serialized.s()).isNotNull();
        assertThat(serialized.s()).doesNotContain("null");

        List<Pokemon> deserialized = converter.transformTo(serialized);

        assertThat(deserialized).hasSize(2);

        Pokemon bulbasaur = deserialized.get(0);
        assertThat(bulbasaur.index()).isEqualTo("1");
        assertThat(bulbasaur.name()).isEqualTo("bulbasaur");
        assertThat(bulbasaur.image()).isEqualTo("https://pokeapi.co/img/1.png");
        assertThat(bulbasaur.types()).containsExactly("grass", "poison");
        assertThat(bulbasaur.abilities()).hasSize(2);
        assertThat(bulbasaur.abilities().get(0).name()).isEqualTo("hp");
        assertThat(bulbasaur.abilities().get(0).strength()).isEqualByComparingTo(BigDecimal.valueOf(45));

        Pokemon charmander = deserialized.get(1);
        assertThat(charmander.index()).isEqualTo("4");
        assertThat(charmander.name()).isEqualTo("charmander");
        assertThat(charmander.types()).containsExactly("fire");
        assertThat(charmander.abilities().get(1).name()).isEqualTo("speed");
        assertThat(charmander.abilities().get(1).strength()).isEqualByComparingTo(BigDecimal.valueOf(65));
    }

    @Test
    void shouldReturnNullAttributeWhenListIsNull() {
        AttributeValue result = converter.transformFrom(null);
        assertThat(result.nul()).isTrue();
    }

    @Test
    void shouldReturnEmptyListWhenAttributeIsNull() {
        List<Pokemon> result = converter.transformTo(AttributeValue.builder().nul(true).build());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotContainNullElementsAfterDeserialization() {
        List<Pokemon> team = List.of(
                new Pokemon("25", "pikachu", "https://pokeapi.co/img/25.png",
                        List.of("electric"),
                        List.of(new Power("speed", BigDecimal.valueOf(90)))),
                new Pokemon("6", "charizard", "https://pokeapi.co/img/6.png",
                        List.of("fire", "flying"),
                        List.of(new Power("attack", BigDecimal.valueOf(84))))
        );

        AttributeValue serialized = converter.transformFrom(team);
        List<Pokemon> deserialized = converter.transformTo(serialized);

        assertThat(deserialized).doesNotContainNull();
        assertThat(deserialized).allSatisfy(p -> {
            assertThat(p.index()).isNotNull();
            assertThat(p.name()).isNotNull();
            assertThat(p.abilities()).isNotNull().isNotEmpty();
        });
    }
}
