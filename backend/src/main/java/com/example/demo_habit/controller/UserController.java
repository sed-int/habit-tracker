package com.example.demo_habit.controller;

import com.example.demo_habit.dto.UserRequest;
import com.example.demo_habit.service.UserService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class UserController {
    private final UserService userService;

    // TODO: ResponseEntity to DTO
    @PostMapping("/users")
    public ResponseEntity<Map<String, Long>> create(@RequestBody @Valid UserRequest request) {
        Long id = userService.register(request);
        return ResponseEntity
            .created(URI.create("/users/" + id))
            .body(Map.of("id", id));
    }

//    public ResponseEntity<Map<String, Long>> login() {
//        // issue jwt and return
//        return null;
//    }

//    public ResponseEntity<Map<String, Long>> logout() {
//        // insert the token into revoked token map
//        return null;
//    }

//    public ResponseEntity<Map<String, Long>> refreshAccessToken() {
//        // refresh access token by refresh token
//        return null;
//    }
}
