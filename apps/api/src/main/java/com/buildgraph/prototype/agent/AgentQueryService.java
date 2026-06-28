package com.buildgraph.prototype.agent;

import com.buildgraph.prototype.common.DbValueMapper;
import com.buildgraph.prototype.common.MockData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AgentQueryService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;

    public AgentQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> createSession(Map<String, Object> request) {
        String requirementId = stringOrNull(request == null ? null : request.get("requirementId"));
        String buildId = stringOrNull(request == null ? null : request.get("buildId"));
        String asTicketId = stringOrNull(request == null ? null : request.get("asTicketId"));
        int rootCount = (requirementId == null ? 0 : 1) + (buildId == null ? 0 : 1) + (asTicketId == null ? 0 : 1);
        if (rootCount > 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "requirementId, buildId, asTicketId 중 하나만 보낼 수 있습니다.");
        }
        if (requirementId == null && buildId == null && asTicketId == null) {
            requirementId = latestRequirementId();
        }
        Map<String, Object> row = jdbcTemplate.queryForMap("""
                INSERT INTO agent_sessions (
                  user_id,
                  requirement_id,
                  build_id,
                  as_ticket_id,
                  status,
                  state_timeline
                )
                VALUES (
                  (SELECT id FROM users WHERE email = 'user@example.com'),
                  (SELECT id FROM requirements WHERE public_id = ?::uuid),
                  (SELECT id FROM builds WHERE public_id = ?::uuid),
                  (SELECT id FROM as_tickets WHERE public_id = ?::uuid),
                  'QUEUED',
                  ?::jsonb
                )
                RETURNING public_id::text AS id, status, created_at
                """, requirementId, buildId, asTicketId, json(List.of(timelineItem(null, "QUEUED", "SYSTEM", "session created"))));
        String id = DbValueMapper.string(row, "id");
        return MockData.map(
                "id", id,
                "status", DbValueMapper.string(row, "status"),
                "mode", "LIMITED_ORCHESTRATOR",
                "nextAction", "/api/agent/sessions/" + id + "/run"
        );
    }

    public Map<String, Object> runSession(String id) {
        Map<String, Object> timeline = MockData.map(
                "timeline", List.of(
                        timelineItem(null, "QUEUED", "SYSTEM", "session created"),
                        timelineItem("QUEUED", "RUNNING", "SYSTEM", "agent run requested"),
                        timelineItem("RUNNING", "RAG_SEARCHED", "SYSTEM", "evidence retrieved"),
                        timelineItem("RAG_SEARCHED", "TOOLS_CALLED", "SYSTEM", "tools completed"),
                        timelineItem("TOOLS_CALLED", "SUMMARY_READY", "SYSTEM", "summary generated")
                )
        );
        jdbcTemplate.update("""
                UPDATE agent_sessions
                SET status = 'SUMMARY_READY',
                    summary = COALESCE(summary, 'DB-backed prototype agent summary.'),
                    state_timeline = ?::jsonb,
                    updated_at = now()
                WHERE public_id = ?::uuid
                """, json(timeline.get("timeline")), id);
        return session(id);
    }

    public Map<String, Object> session(String id) {
        Map<String, Object> row = agentSessionRow(id);
        return MockData.map(
                "id", DbValueMapper.string(row, "id"),
                "status", DbValueMapper.string(row, "status"),
                "stateTimeline", DbValueMapper.json(row, "state_timeline", List.of()),
                "summary", DbValueMapper.string(row, "summary"),
                "toolInvocations", toolInvocationsBySession(id),
                "ragEvidence", ragEvidenceBySession(id)
        );
    }

    public Map<String, Object> adminSession(String id) {
        Map<String, Object> row = agentSessionRow(id);
        return MockData.map(
                "id", DbValueMapper.string(row, "id"),
                "status", DbValueMapper.string(row, "status"),
                "summary", DbValueMapper.string(row, "summary"),
                "stateTimeline", DbValueMapper.json(row, "state_timeline", List.of()),
                "toolInvocations", toolInvocationsBySession(id),
                "evidenceIds", ragEvidenceBySession(id).stream().map(evidence -> evidence.get("id")).toList()
        );
    }

    public Map<String, Object> agentSessions() {
        List<Map<String, Object>> items = jdbcTemplate.queryForList("""
                        SELECT s.public_id::text AS id,
                               s.status,
                               u.public_id::text AS user_id,
                               s.created_at
                        FROM agent_sessions s
                        JOIN users u ON u.id = s.user_id
                        ORDER BY s.created_at DESC, s.id DESC
                        """)
                .stream()
                .map(row -> MockData.map(
                        "id", DbValueMapper.string(row, "id"),
                        "status", DbValueMapper.string(row, "status"),
                        "userId", DbValueMapper.string(row, "user_id"),
                        "createdAt", DbValueMapper.timestamp(row, "created_at")
                ))
                .toList();
        return MockData.map("items", items, "page", 0, "size", 20, "total", items.size());
    }

    public Map<String, Object> toolInvocations() {
        List<Map<String, Object>> items = jdbcTemplate.queryForList(toolInvocationSql() + " ORDER BY ti.created_at DESC, ti.id DESC")
                .stream()
                .map(this::toolInvocationMap)
                .toList();
        return MockData.map("items", items, "page", 0, "size", 20, "total", items.size());
    }

    public Map<String, Object> toolInvocation(String id) {
        return jdbcTemplate.queryForList(toolInvocationSql() + " WHERE ti.public_id = ?::uuid", id)
                .stream()
                .findFirst()
                .map(this::toolInvocationMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tool invocation을 찾을 수 없습니다."));
    }

    public Map<String, Object> ragEvidence(String id) {
        return jdbcTemplate.queryForList("""
                        SELECT re.public_id::text AS id,
                               s.public_id::text AS agent_session_id,
                               re.source_id,
                               re.chunk_text,
                               re.summary,
                               re.score,
                               re.metadata
                        FROM rag_evidence re
                        LEFT JOIN agent_sessions s ON s.id = re.agent_session_id
                        WHERE re.public_id = ?::uuid
                        """, id)
                .stream()
                .findFirst()
                .map(this::ragEvidenceMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RAG 근거를 찾을 수 없습니다."));
    }

    public Map<String, Object> ragSearch() {
        List<Map<String, Object>> items = jdbcTemplate.queryForList("""
                        SELECT re.public_id::text AS id,
                               s.public_id::text AS agent_session_id,
                               re.source_id,
                               re.chunk_text,
                               re.summary,
                               re.score,
                               re.metadata
                        FROM rag_evidence re
                        LEFT JOIN agent_sessions s ON s.id = re.agent_session_id
                        ORDER BY re.score DESC NULLS LAST, re.id
                        """)
                .stream()
                .map(this::ragEvidenceMap)
                .toList();
        return MockData.map("items", items, "page", 0, "size", 20, "total", items.size());
    }

    private Map<String, Object> agentSessionRow(String id) {
        return jdbcTemplate.queryForList("""
                        SELECT public_id::text AS id, status, summary, state_timeline, created_at, updated_at
                        FROM agent_sessions
                        WHERE public_id = ?::uuid
                        """, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent session을 찾을 수 없습니다."));
    }

    private List<Map<String, Object>> toolInvocationsBySession(String sessionId) {
        return jdbcTemplate.queryForList(toolInvocationSql() + " WHERE s.public_id = ?::uuid ORDER BY ti.id", sessionId)
                .stream()
                .map(this::toolInvocationMap)
                .toList();
    }

    private List<Map<String, Object>> ragEvidenceBySession(String sessionId) {
        return jdbcTemplate.queryForList("""
                        SELECT re.public_id::text AS id,
                               s.public_id::text AS agent_session_id,
                               re.source_id,
                               re.chunk_text,
                               re.summary,
                               re.score,
                               re.metadata
                        FROM rag_evidence re
                        JOIN agent_sessions s ON s.id = re.agent_session_id
                        WHERE s.public_id = ?::uuid
                        ORDER BY re.id
                        """, sessionId)
                .stream()
                .map(this::ragEvidenceMap)
                .toList();
    }

    private String toolInvocationSql() {
        return """
                SELECT ti.public_id::text AS id,
                       s.public_id::text AS agent_session_id,
                       ti.tool_name,
                       ti.status,
                       ti.confidence,
                       ti.summary,
                       ti.request_payload,
                       ti.result_payload,
                       ti.latency_ms,
                       ti.created_at
                FROM tool_invocations ti
                JOIN agent_sessions s ON s.id = ti.agent_session_id
                """;
    }

    private Map<String, Object> toolInvocationMap(Map<String, Object> row) {
        return MockData.map(
                "id", DbValueMapper.string(row, "id"),
                "agentSessionId", DbValueMapper.string(row, "agent_session_id"),
                "toolName", DbValueMapper.string(row, "tool_name"),
                "status", DbValueMapper.string(row, "status"),
                "confidence", DbValueMapper.string(row, "confidence"),
                "summary", DbValueMapper.string(row, "summary"),
                "latencyMs", DbValueMapper.integer(row, "latency_ms"),
                "requestPayload", DbValueMapper.json(row, "request_payload", Map.of()),
                "resultPayload", DbValueMapper.json(row, "result_payload", Map.of()),
                "createdAt", DbValueMapper.timestamp(row, "created_at")
        );
    }

    private Map<String, Object> ragEvidenceMap(Map<String, Object> row) {
        return MockData.map(
                "id", DbValueMapper.string(row, "id"),
                "agentSessionId", DbValueMapper.string(row, "agent_session_id"),
                "sourceId", DbValueMapper.string(row, "source_id"),
                "chunkText", DbValueMapper.string(row, "chunk_text"),
                "summary", DbValueMapper.string(row, "summary"),
                "score", row.get("score"),
                "metadata", DbValueMapper.json(row, "metadata", Map.of())
        );
    }

    private String latestRequirementId() {
        List<String> rows = jdbcTemplate.queryForList("""
                SELECT public_id::text
                FROM requirements
                ORDER BY created_at DESC, id DESC
                LIMIT 1
                """, String.class);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private static Map<String, Object> timelineItem(String from, String to, String actor, String reason) {
        return MockData.map("from", from, "to", to, "at", MockData.now(), "actor", actor, "reason", reason);
    }

    private static String stringOrNull(Object value) {
        return value == null ? null : value.toString();
    }

    private static String json(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON 변환에 실패했습니다.", e);
        }
    }
}
