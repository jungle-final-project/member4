package com.buildgraph.prototype.ticket;

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
public class TicketController {
    @PostMapping("/as-tickets")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> create() {
        return MockData.map("ticketId", "AS-1031", "status", "OPEN");
    }

    @GetMapping("/as-tickets/{id}")
    Map<String, Object> ticket(@PathVariable String id) {
        return MockData.map("id", id, "status", "OPEN", "symptom", "게임 중 프레임 급락", "causeCandidates", MockData.tickets());
    }
}
