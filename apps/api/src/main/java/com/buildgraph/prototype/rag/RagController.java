package com.buildgraph.prototype.rag;

import com.buildgraph.prototype.common.MockData;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RagController {
    @GetMapping("/rag/search")
    Map<String, Object> search() {
        return MockData.map("items", List.of(
                MockData.map("sourceId", "psu-rule-001", "summary", "GPU 피크 전력과 CPU TDP 합산 후 여유율 적용", "score", 0.91),
                MockData.map("sourceId", "qhd-gaming-4070s", "summary", "QHD 게임 기준 GPU 우선 구성 근거", "score", 0.84)
        ));
    }
}
