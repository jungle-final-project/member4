package com.buildgraph.prototype.part;

import com.buildgraph.prototype.common.MockData;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PartController {
    @GetMapping("/parts")
    Map<String, Object> parts() {
        return MockData.map("items", MockData.parts());
    }

    @GetMapping("/parts/{id}")
    Map<String, Object> part(@PathVariable String id) {
        return MockData.parts().stream()
                .filter(part -> id.equals(part.get("id")))
                .findFirst()
                .orElse(MockData.map("id", id, "status", "NOT_FOUND"));
    }

    @PostMapping("/tools/{tool}/check")
    Map<String, Object> tool(@PathVariable String tool) {
        return MockData.toolResult(tool);
    }
}
