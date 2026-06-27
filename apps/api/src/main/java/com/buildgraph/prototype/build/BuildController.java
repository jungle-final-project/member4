package com.buildgraph.prototype.build;

import com.buildgraph.prototype.common.MockData;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BuildController {
    @PostMapping("/requirements/parse")
    Map<String, Object> parse(@RequestBody Map<String, Object> request) {
        return MockData.map(
                "id", "req-1001",
                "rawMessage", request.getOrDefault("message", "QHD 게임용 PC"),
                "budget", request.getOrDefault("budget", 2000000),
                "usage", List.of("gaming", "development"),
                "targetResolution", "QHD",
                "brandPreference", List.of("NVIDIA"),
                "missingFields", List.of()
        );
    }

    @PostMapping("/builds/recommend")
    Map<String, Object> recommend() {
        return MockData.map("builds", MockData.builds());
    }

    @GetMapping("/builds/{id}")
    Map<String, Object> build(@PathVariable String id) {
        return MockData.map("id", id, "name", "QHD 게임 균형형", "totalPrice", 1980000, "items", MockData.parts(), "toolResults", List.of(MockData.toolResult("compatibility"), MockData.toolResult("power")));
    }

    @GetMapping("/builds/history")
    Map<String, Object> history() {
        return MockData.map("items", MockData.builds());
    }

    @PostMapping("/builds/{id}/change-part")
    Map<String, Object> changePart(@PathVariable String id) {
        return MockData.map(
                "buildId", id,
                "status", "WARN",
                "summary", "GPU 성능은 개선되지만 PSU 여유율 확인이 필요합니다.",
                "diff", List.of(
                        MockData.map("metric", "price", "before", 1662000, "after", 1980000, "delta", 318000),
                        MockData.map("metric", "qhdPerformance", "before", "1.0x", "after", "1.42x", "delta", "+42%")
                )
        );
    }
}
