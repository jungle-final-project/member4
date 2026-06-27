package com.buildgraph.prototype.log;

import com.buildgraph.prototype.common.MockData;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentLogController {
    @PostMapping("/agent-logs/upload")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> upload() {
        return MockData.map("logUploadId", "log-1001", "status", "UPLOADED", "rangeMinutes", 30);
    }

    @GetMapping("/agent-logs/{id}")
    Map<String, Object> log(@PathVariable String id) {
        return MockData.map("id", id, "status", "UPLOADED", "summary", "GPU 88도, 사용률 96%, VRAM 89% 관측");
    }
}
