package com.buildgraph.prototype.user;

import com.buildgraph.prototype.common.MockData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @PostMapping("/auth/login")
    Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        String role = request.email().startsWith("admin") ? "ADMIN" : "USER";
        return MockData.map(
                "token", "demo-jwt-" + role.toLowerCase(),
                "user", MockData.map("id", role.equals("ADMIN") ? "admin-001" : "user-1004", "email", request.email(), "role", role)
        );
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> signup(@Valid @RequestBody SignupRequest request) {
        return MockData.map("id", "user-1004", "email", request.email(), "name", request.name(), "role", "USER", "createdAt", MockData.now());
    }

    @GetMapping("/auth/me")
    Map<String, Object> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        boolean admin = authorization != null && authorization.contains("admin");
        return MockData.map("id", admin ? "admin-001" : "user-1004", "email", admin ? "admin@example.com" : "user@example.com", "role", admin ? "ADMIN" : "USER");
    }

    record LoginRequest(@Email String email, @NotBlank String password) {
    }

    record SignupRequest(@NotBlank String name, @Email String email, @NotBlank String password) {
    }
}
