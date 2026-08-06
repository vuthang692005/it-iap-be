package com.example.it_iap.config;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;

import tools.jackson.databind.json.JsonMapper;

public final class Jackson3JsonFormatMapper implements FormatMapper {

    private final JsonMapper jsonMapper;

    public Jackson3JsonFormatMapper(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public <T> T fromString(
            CharSequence json,
            JavaType<T> javaType,
            WrapperOptions wrapperOptions
    ) {
        if (json == null) {
            return null;
        }

        var targetType = jsonMapper.constructType(
                javaType.getJavaType()
        );

        return jsonMapper.readValue(
                json.toString(),
                targetType
        );
    }

    @Override
    public <T> String toString(
            T value,
            JavaType<T> javaType,
            WrapperOptions wrapperOptions
    ) {
        if (value == null) {
            return null;
        }

        var targetType = jsonMapper.constructType(
                javaType.getJavaType()
        );

        return jsonMapper
                .writerFor(targetType)
                .writeValueAsString(value);
    }
}