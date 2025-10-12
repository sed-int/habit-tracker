package com.example.demo_habit.controller;

import com.example.demo_habit.domain.Habit;
import com.example.demo_habit.dto.HabitRequest;
import com.example.demo_habit.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/v1/habits")
public class HabitController {
    private final HabitService habitService;

    // TODO: Extract userId from JWT token in authentication context
    // For now, using request parameter
    @PostMapping
    public ResponseEntity<Map<String, Long>> createHabit(
            @RequestParam Long userId,
            @RequestBody @Valid HabitRequest request) {
        Long habitId = habitService.createHabit(userId, request);
        return ResponseEntity
                .created(URI.create("/api/v1/habits/" + habitId))
                .body(Map.of("id", habitId));
    }

    @GetMapping
    public ResponseEntity<List<Habit>> getHabits(@RequestParam Long userId) {
        List<Habit> habits = habitService.getHabitsByUser(userId);
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabit(
            @PathVariable Long id,
            @RequestParam Long userId) {
        Habit habit = habitService.getHabitById(id, userId);
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateHabit(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody @Valid HabitRequest request) {
        habitService.updateHabit(id, userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long id,
            @RequestParam Long userId) {
        habitService.deleteHabit(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Void> archiveHabit(
            @PathVariable Long id,
            @RequestParam Long userId) {
        habitService.archiveHabit(id, userId);
        return ResponseEntity.noContent().build();
    }
}