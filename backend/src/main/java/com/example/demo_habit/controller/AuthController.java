package com.example.demo_habit.controller;

import com.example.demo_habit.dto.LoginRequest;
import com.example.demo_habit.dto.LoginResponse;
import com.example.demo_habit.dto.UserRequest;
import com.example.demo_habit.service.AuthService;
import com.example.demo_habit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid UserRequest request) {
        Long userId = userService.register(request);
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + userId))
                .body(Map.of("id", userId, "message", "User registered successfully"));
    }

    // TODO: Implement logout with token revocation
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // TODO: Implement refresh token mechanism
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshAccessToken() {
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED, "Refresh token not yet implemented");
    }
}