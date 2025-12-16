package com.stream.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Debug controller to diagnose P6Spy configuration
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DebugController {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Check DataSource and Driver configuration
     */
    @GetMapping("/datasource-info")
    public Map<String, Object> getDatasourceInfo() {
        Map<String, Object> info = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            info.put("url", metaData.getURL());
            info.put("driverName", metaData.getDriverName());
            info.put("driverVersion", metaData.getDriverVersion());
            info.put("databaseProductName", metaData.getDatabaseProductName());
            info.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            info.put("connectionClass", conn.getClass().getName());

            log.info("=== DataSource Info ===");
            log.info("URL: {}", metaData.getURL());
            log.info("Driver Name: {}", metaData.getDriverName());
            log.info("Connection Class: {}", conn.getClass().getName());

            // Check if P6Spy is wrapping the connection
            boolean isP6Spy = conn.getClass().getName().contains("p6spy");
            info.put("isP6SpyActive", isP6Spy);

            if (isP6Spy) {
                log.info("✅ P6Spy IS ACTIVE - Connection is wrapped by P6Spy");
            } else {
                log.warn("❌ P6Spy NOT ACTIVE - Using direct PostgreSQL connection");
                log.warn("Expected connection class to contain 'p6spy' but got: {}", conn.getClass().getName());
            }

        } catch (Exception e) {
            log.error("Error getting datasource info", e);
            info.put("error", e.getMessage());
        }

        return info;
    }

    /**
     * Execute a simple query with explicit logging
     */
    @GetMapping("/execute-query")
    public Map<String, Object> executeQuery() {
        log.info(">>> Executing test query...");

        Map<String, Object> result = new HashMap<>();

        try {
            // Execute a simple query
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

            log.info(">>> Query executed successfully. Count: {}", count);
            result.put("success", true);
            result.put("userCount", count);
            result.put("message", "Check logs above for SQL statement");

        } catch (Exception e) {
            log.error(">>> Query execution failed", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }
}
