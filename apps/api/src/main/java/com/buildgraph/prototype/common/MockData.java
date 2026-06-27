package com.buildgraph.prototype.common;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MockData {
    private MockData() {
    }

    public static Map<String, Object> map(Object... entries) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            result.put(String.valueOf(entries[i]), entries[i + 1]);
        }
        return result;
    }

    public static List<Map<String, Object>> parts() {
        return List.of(
                map("id", "cpu-7600", "category", "CPU", "name", "AMD Ryzen 5 7600", "price", 259000, "status", "ACTIVE"),
                map("id", "mb-b650", "category", "MAINBOARD", "name", "B650M WiFi", "price", 179000, "status", "ACTIVE"),
                map("id", "ram-32", "category", "RAM", "name", "DDR5 32GB 5600", "price", 128000, "status", "ACTIVE"),
                map("id", "gpu-4070s", "category", "GPU", "name", "RTX 4070 SUPER 12GB", "price", 890000, "status", "ACTIVE"),
                map("id", "ssd-1tb", "category", "SSD", "name", "NVMe Gen4 1TB", "price", 99000, "status", "ACTIVE"),
                map("id", "psu-750", "category", "PSU", "name", "750W 80+ Gold", "price", 126000, "status", "ACTIVE")
        );
    }

    public static List<Map<String, Object>> builds() {
        return List.of(
                map("id", "bg-1001", "name", "QHD 게임 균형형", "totalPrice", 1980000, "confidence", "MEDIUM", "warnings", List.of("PSU 여유율 확인 필요")),
                map("id", "bg-1002", "name", "개발 + 게임 혼합형", "totalPrice", 2120000, "confidence", "HIGH", "warnings", List.of("RAM 32GB 권장")),
                map("id", "bg-1003", "name", "AI 실습 입문형", "totalPrice", 1620000, "confidence", "MEDIUM", "warnings", List.of("VRAM 한계 가능성"))
        );
    }

    public static Map<String, Object> toolResult(String tool) {
        return map(
                "tool", tool,
                "status", "compatibility".equals(tool) || "size".equals(tool) ? "PASS" : "WARN",
                "score", 0.82,
                "confidence", "MEDIUM",
                "summary", "Prototype seed result for " + tool,
                "warnings", List.of("seed 기반 결과입니다"),
                "evidence", List.of(map("source_id", tool + "-rule-001", "summary", "Tool 검증 근거 seed"))
        );
    }

    public static List<Map<String, Object>> tickets() {
        return List.of(
                map("id", "AS-1031", "user", "user@example.com", "symptom", "게임 중 프레임 급락", "status", "OPEN", "cause", "GPU 온도 과열 가능성"),
                map("id", "AS-1032", "user", "dev@example.com", "symptom", "IDE 실행 시 메모리 부족", "status", "IN_PROGRESS", "cause", "RAM 사용률 92% 반복")
        );
    }

    public static String now() {
        return OffsetDateTime.now().toString();
    }
}
