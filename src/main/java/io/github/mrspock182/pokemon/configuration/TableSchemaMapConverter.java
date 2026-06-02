package io.github.mrspock182.pokemon.configuration;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class TableSchemaMapConverter<T> implements AttributeConverter<T> {
    private final TableSchema<T> schema;
    private final EnhancedType<T> type;

    public TableSchemaMapConverter(TableSchema<T> schema, Class<T> clazz) {
        this.schema = schema;
        this.type = EnhancedType.of(clazz);
    }

    @Override
    public AttributeValue transformFrom(T input) {
        if (input == null) {
            return AttributeValue.builder().nul(true).build();
        }
        return AttributeValue.builder()
                .m(schema.itemToMap(input, true))
                .build();
    }

    @Override
    public T transformTo(AttributeValue input) {
        if (input == null || input.m() == null) {
            return null;
        }
        return schema.mapToItem(input.m());
    }

    @Override
    public EnhancedType<T> type() {
        return type;
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }
}