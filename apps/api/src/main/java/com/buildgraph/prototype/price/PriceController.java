package com.buildgraph.prototype.price;

import com.buildgraph.prototype.common.MockData;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PriceController {
    @GetMapping("/price-alerts")
    Map<String, Object> alerts() {
        return MockData.map("items", List.of(MockData.map("id", "alert-001", "partId", "gpu-4070s", "targetPrice", 850000, "status", "ACTIVE")));
    }

    @PostMapping("/price-alerts")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> createAlert() {
        return MockData.map("priceAlertId", "alert-001", "status", "ACTIVE");
    }
}
