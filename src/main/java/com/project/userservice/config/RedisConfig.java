package com.project.userservice.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            // 1. Создаем и настраиваем ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Поддержка Java 8 Date/Time API (LocalDate, LocalDateTime и т.д.)
            objectMapper.registerModule(new JavaTimeModule());

            // Игнорируем новые поля при десериализации (защита при деплое новых версий DTO)
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // 2. Создаем JSON-сериализатор через стандартную фабрику Spring Data Redis,
            // передавая ей наш готовый ObjectMapper. Это заменяет устаревший GenericJackson2JsonRedisSerializer.
            RedisSerializer<Object> jsonSerializer = RedisSerializer.json();

            // 3. Настраиваем глобальные параметры кэша
            RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10)) // TTL по умолчанию
                    .serializeKeysWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                    )
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
                    )
                    .disableCachingNullValues();

            builder.cacheDefaults(defaultCacheConfig);
        };
    }
}