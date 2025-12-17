package com.stream.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stream.demo.model.dto.cache.SessionCacheDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Configuration
 * <p>
 * Factory pattern để tạo type-safe RedisTemplate cho từng DTO.
 * Best practice: Dùng Jackson2JsonRedisSerializer thay vì
 * GenericJackson2JsonRedisSerializer.
 */
@Configuration
public class RedisConfig {

    /**
     * Shared ObjectMapper cho tất cả Redis serializers
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Factory method để tạo type-safe RedisTemplate
     * <p>
     * Dùng Jackson2JsonRedisSerializer với explicit type → type-safe
     * deserialization
     *
     * @param factory RedisConnectionFactory
     * @param clazz   Target class type
     * @return Type-safe RedisTemplate
     */
    private <T> RedisTemplate<String, T> createRedisTemplate(
            RedisConnectionFactory factory, Class<T> clazz) {

        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key serializer
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value serializer - TYPE-SPECIFIC
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(createObjectMapper(), clazz);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * RedisTemplate for SessionCacheDTO
     * Type-safe, no casting needed
     */
    @Bean
    public RedisTemplate<String, SessionCacheDTO> sessionCacheRedisTemplate(
            RedisConnectionFactory factory) {
        return createRedisTemplate(factory, SessionCacheDTO.class);
    }

    // Future: Add more type-specific templates as needed
    // @Bean
    // public RedisTemplate<String, AnotherDTO> anotherDtoRedisTemplate(
    // RedisConnectionFactory factory) {
    // return createRedisTemplate(factory, AnotherDTO.class);
    // }
}
