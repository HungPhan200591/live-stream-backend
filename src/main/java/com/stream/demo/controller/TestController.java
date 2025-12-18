package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.model.entity.User;
import com.stream.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Testing", description = "Infrastructure testing endpoints")
public class TestController {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final RabbitTemplate rabbitTemplate;
	private final UserRepository userRepository;

    /**
     * Test PostgreSQL connection
     */
    @GetMapping("/sql")
    @Operation(summary = "Test PostgreSQL connection")
    public ApiResponse<String> testSQL() {
        String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
        return ApiResponse.success(version, "PostgreSQL connected");
    }

    /**
     * Test Redis connection
     */
    @GetMapping("/redis")
    @Operation(summary = "Test Redis connection")
    public ApiResponse<String> testRedis() {
        redisTemplate.opsForValue().set("test:ping", "pong", 10, TimeUnit.SECONDS);
        String result = redisTemplate.opsForValue().get("test:ping");
        return ApiResponse.success(result, "Redis connected");
    }

    /**
     * Test RabbitMQ connection
     */
    @GetMapping("/rabbitmq")
    @Operation(summary = "Test RabbitMQ connection")
    public ApiResponse<String> testRabbitMQ() {
        String testMessage = "Test message at " + LocalDateTime.now();
        rabbitTemplate.convertAndSend("test.queue", testMessage);
        return ApiResponse.success(testMessage, "RabbitMQ connected");
    }

	/**
	 * Test endpoint to trigger SQL query and verify P6Spy logging
	 * This will execute a SELECT query that should be logged by P6Spy
	 */
	@GetMapping("/sql")
	@Operation(summary = "Test SQL Logging", description = "Test endpoint to verify P6Spy SQL logging with actual parameter values")
	public String testSqlLogging() {
		log.info(">>> Testing P6Spy SQL logging...");

		// Trigger a simple SELECT query
		List<User> users = userRepository.findAll();

		log.info(">>> Found {} users in database", users.size());

		return "P6Spy Test: Found " + users.size() + " users. Check console logs!";
	}
}
