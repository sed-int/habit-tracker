package com.example.demo_habit.controller;

import com.example.demo_habit.dto.HabitCompletionRequest;
import com.example.demo_habit.dto.HabitCompletionResponse;
import com.example.demo_habit.service.HabitCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/api/v1/habit-completions")
public class HabitCompletionController {
    private final HabitCompletionService habitCompletionService;

    // TODO: Extract userId from JWT token in authentication context
    // For now, using request parameter
    @PostMapping
    public ResponseEntity<Map<String, Long>> createCompletion(
            @RequestParam Long userId,
            @RequestBody @Valid HabitCompletionRequest request) {
        Long completionId = habitCompletionService.createCompletion(userId, request);
        return ResponseEntity
                .created(URI.create("/api/v1/habit-completions/" + completionId))
                .body(Map.of("id", completionId));
    }

    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<HabitCompletionResponse>> getCompletionsByHabit(
            @PathVariable Long habitId,
            @RequestParam Long userId) {
        List<HabitCompletionResponse> completions = habitCompletionService.getCompletionsByHabit(habitId, userId);
        return ResponseEntity.ok(completions);
    }

    @GetMapping("/habit/{habitId}/range")
    public ResponseEntity<List<HabitCompletionResponse>> getCompletionsByHabitAndDateRange(
            @PathVariable Long habitId,
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<HabitCompletionResponse> completions = habitCompletionService.getCompletionsByHabitAndDateRange(
                habitId, userId, startDate, endDate);
        return ResponseEntity.ok(completions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitCompletionResponse> getCompletion(
            @PathVariable Long id,
            @RequestParam Long userId) {
        // This method still needs entity, so we'll skip DTO for now
        throw new RuntimeException("Not implemented");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCompletion(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody @Valid HabitCompletionRequest request) {
        habitCompletionService.updateCompletion(id, userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompletion(
            @PathVariable Long id,
            @RequestParam Long userId) {
        habitCompletionService.deleteCompletion(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/habit/{habitId}/count")
    public ResponseEntity<Map<String, Long>> getCompletionCount(
            @PathVariable Long habitId,
            @RequestParam Long userId) {
        long count = habitCompletionService.getCompletionCount(habitId, userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}