package com.buildgraph.prototype.common;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    Map<String, Object> health() {
        Integer db = jdbcTemplate.queryForObject("select 1", Integer.class);
        return Map.of("status", "UP", "database", db == 1 ? "UP" : "UNKNOWN");
    }
}
