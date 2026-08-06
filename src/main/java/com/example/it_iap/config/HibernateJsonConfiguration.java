package com.example.it_iap.config;

import org.hibernate.cfg.MappingSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.json.JsonMapper;

@Configuration(proxyBeanMethods = false)
public class HibernateJsonConfiguration {

    @Bean
    public HibernatePropertiesCustomizer hibernateJsonFormatMapper(
            JsonMapper jsonMapper
    ) {
        Jackson3JsonFormatMapper formatMapper =
                new Jackson3JsonFormatMapper(jsonMapper);

        return properties -> properties.put(
                MappingSettings.JSON_FORMAT_MAPPER,
                formatMapper
        );
    }
}