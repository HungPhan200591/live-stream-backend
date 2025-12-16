package com.stream.demo.controller;

import com.stream.demo.model.entity.User;
import com.stream.demo.repository.UserRepository;
import com.stream.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Test controller to verify P6Spy SQL logging
 * Access: GET http://localhost:8080/api/test/sql
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test", description = "Development test endpoints for SQL logging verification")
public class TestController {

    private final UserRepository userRepository;

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
