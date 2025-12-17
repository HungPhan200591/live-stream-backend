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
 * Type-safe RedisTemplate beans với shared ObjectMapper và factory method.
 * Simple, explicit, maintainable - theo Spring best practices.
 */
@Configuration
public class RedisConfig {

    /**
     * Redis Template Bean Names
     * Constants cho @Qualifier - centralized, type-safe
     */
    public static class RedisTemplateBeanNames {
	    private RedisTemplateBeanNames() {}

	    public static final String SESSION_CACHE = "sessionCacheRedisTemplate";
        // Add more bean names here
        // public static final String USER_PROFILE = "userProfileRedisTemplate";
    }

    /**
     * Shared ObjectMapper cho tất cả Redis serializers
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // ========== Bean Definitions ==========
    // Copy-paste pattern: 3 lines per DTO type

    @Bean(name = RedisTemplateBeanNames.SESSION_CACHE)
    public RedisTemplate<String, SessionCacheDTO> sessionCacheRedisTemplate(
            RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
        return createRedisTemplate(factory, redisObjectMapper, SessionCacheDTO.class);
    }

    // Add more beans here (simple copy-paste):
    // @Bean(name = RedisTemplateBeanNames.USER_PROFILE)
    // public RedisTemplate<String, UserProfileDTO> userProfileRedisTemplate(
    // RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
    // return createRedisTemplate(factory, redisObjectMapper, UserProfileDTO.class);
    // }

    /**
     * Helper method để tạo type-safe RedisTemplate
     * Reuse logic, avoid duplication
     */
    private <T> RedisTemplate<String, T> createRedisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper objectMapper,
            Class<T> clazz) {

        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
