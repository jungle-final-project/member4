package com.buildgraph.prototype.part;

import com.buildgraph.prototype.common.DbValueMapper;
import com.buildgraph.prototype.common.MockData;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class PartQueryService {
    private final JdbcTemplate jdbcTemplate;

    public PartQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> parts() {
        return MockData.map("items", partRows());
    }

    public Map<String, Object> part(String id) {
        return jdbcTemplate.queryForList("""
                        SELECT public_id::text AS id, category, name, manufacturer, price, status, attributes
                        FROM parts
                        WHERE public_id = ?::uuid
                          AND deleted_at IS NULL
                        """, id)
                .stream()
                .findFirst()
                .map(this::partMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "부품을 찾을 수 없습니다."));
    }

    public Map<String, Object> toolResult(String toolName) {
        Map<String, Object> rule = ruleFor(toolName);
        String status = rule == null ? defaultStatus(toolName) : DbValueMapper.string(rule, "status");
        String summary = rule == null ? "DB seed result for " + toolName : DbValueMapper.string(rule, "summary");
        return MockData.map(
                "status", status,
                "confidence", "MEDIUM",
                "summary", summary,
                "details", MockData.map(
                        "checkedPartIds", partRows().stream().limit(3).map(part -> part.get("id")).toList(),
                        "source", "db-seed",
                        "toolName", toolName
                )
        );
    }

    private List<Map<String, Object>> partRows() {
        return jdbcTemplate.queryForList("""
                        SELECT public_id::text AS id, category, name, manufacturer, price, status, attributes
                        FROM parts
                        WHERE deleted_at IS NULL
                        ORDER BY
                          CASE category
                            WHEN 'CPU' THEN 1
                            WHEN 'MOTHERBOARD' THEN 2
                            WHEN 'RAM' THEN 3
                            WHEN 'GPU' THEN 4
                            WHEN 'STORAGE' THEN 5
                            WHEN 'PSU' THEN 6
                            WHEN 'CASE' THEN 7
                            WHEN 'COOLER' THEN 8
                            ELSE 99
                          END,
                          id
                        """)
                .stream()
                .map(this::partMap)
                .toList();
    }

    private Map<String, Object> partMap(Map<String, Object> row) {
        return MockData.map(
                "id", DbValueMapper.string(row, "id"),
                "category", DbValueMapper.string(row, "category"),
                "name", DbValueMapper.string(row, "name"),
                "manufacturer", DbValueMapper.string(row, "manufacturer"),
                "price", DbValueMapper.integer(row, "price"),
                "status", DbValueMapper.string(row, "status"),
                "attributes", DbValueMapper.json(row, "attributes", Map.of())
        );
    }

    private Map<String, Object> ruleFor(String toolName) {
        String category = switch (toolName) {
            case "compatibility" -> "CPU";
            case "size" -> "GPU";
            case "power" -> "PSU";
            case "performance" -> "GPU";
            case "price" -> "GPU";
            default -> "CPU";
        };
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
                SELECT result_status AS status, message AS summary
                FROM compatibility_rules
                WHERE category = ?
                  AND deleted_at IS NULL
                ORDER BY id
                LIMIT 1
                """, category);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private static String defaultStatus(String toolName) {
        return "compatibility".equals(toolName) || "size".equals(toolName) ? "PASS" : "WARN";
    }
}
