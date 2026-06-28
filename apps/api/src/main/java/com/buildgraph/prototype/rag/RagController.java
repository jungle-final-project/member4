package com.buildgraph.prototype.rag;

import com.buildgraph.prototype.agent.AgentQueryService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RagController {
    private final AgentQueryService agentQueryService;

    public RagController(AgentQueryService agentQueryService) {
        this.agentQueryService = agentQueryService;
    }

    @GetMapping("/rag/search")
    Map<String, Object> search() {
        return agentQueryService.ragSearch();
    }

    @GetMapping("/rag/evidence/{id}")
    Map<String, Object> evidence(@PathVariable String id) {
        return agentQueryService.ragEvidence(id);
    }
}
