package com.buildgraph.prototype.admin;

import com.buildgraph.prototype.common.MockData;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @GetMapping("/dashboard")
    Map<String, Object> dashboard() {
        return MockData.map("llmQueueP95", "18s", "apiP95", "420ms", "asOpen", 12, "recommendationSuccess", "94%");
    }

    @GetMapping("/agent-sessions/{id}")
    Map<String, Object> agentSession(@PathVariable String id) {
        return MockData.map("id", id, "toolInvocations", List.of(MockData.toolResult("compatibility"), MockData.toolResult("power")), "ragEvidence", List.of(MockData.map("sourceId", "psu-rule-001", "summary", "PSU rule seed")));
    }

    @GetMapping("/tool-invocations")
    Map<String, Object> toolInvocations() {
        return MockData.map("items", List.of(MockData.toolResult("compatibility"), MockData.toolResult("power"), MockData.toolResult("price")));
    }

    @GetMapping("/as-tickets")
    Map<String, Object> tickets() {
        return MockData.map("items", MockData.tickets());
    }

    @GetMapping("/price-jobs")
    Map<String, Object> priceJobs() {
        return MockData.map("items", List.of(MockData.map("id", "price-job-001", "status", "IDLE", "lastRunAt", MockData.now())));
    }

    @PostMapping("/price-jobs/run")
    @ResponseStatus(HttpStatus.ACCEPTED)
    Map<String, Object> runPriceJob() {
        return MockData.map("jobId", "price-job-001", "status", "QUEUED");
    }
}
