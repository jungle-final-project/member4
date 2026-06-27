package com.buildgraph.prototype.agent;

import com.buildgraph.prototype.common.MockData;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentController {
    @PostMapping("/agent/sessions")
    Map<String, Object> createSession() {
        return MockData.map("id", "demo-session", "status", "CREATED");
    }

    @PostMapping("/agent/sessions/{id}/run")
    Map<String, Object> runSession(@PathVariable String id) {
        return MockData.map("id", id, "status", "COMPLETED", "toolInvocations", List.of(MockData.toolResult("compatibility"), MockData.toolResult("price")));
    }

    @GetMapping("/agent/sessions/{id}")
    Map<String, Object> getSession(@PathVariable String id) {
        return MockData.map("id", id, "status", "COMPLETED", "summary", "제한된 오케스트레이터 seed 실행 결과");
    }
}
