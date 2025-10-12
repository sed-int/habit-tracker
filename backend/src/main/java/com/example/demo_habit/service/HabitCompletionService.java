package com.example.demo_habit.service;

import com.example.demo_habit.domain.Habit;
import com.example.demo_habit.domain.HabitCompletion;
import com.example.demo_habit.dto.HabitCompletionRequest;
import com.example.demo_habit.dto.HabitCompletionResponse;
import com.example.demo_habit.repository.HabitCompletionRepository;
import com.example.demo_habit.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HabitCompletionService {
    private final HabitCompletionRepository habitCompletionRepository;
    private final HabitRepository habitRepository;

    @Transactional
    public Long createCompletion(Long userId, HabitCompletionRequest request) {
        Habit habit = habitRepository.findById(request.getHabitId())
            .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Verify ownership
        if (!habit.isOwnedBy(userId)) {
            throw new RuntimeException("Unauthorized access to habit");
        }

        // Check if completion already exists for this date
        habitCompletionRepository.findByHabitAndCompletionDate(habit, request.getCompletionDate())
            .ifPresent(existing -> {
                throw new RuntimeException("Completion already exists for this date");
            });

        HabitCompletion completion = HabitCompletion.builder()
            .habit(habit)
            .completionDate(request.getCompletionDate())
            .done(request.getDone())
            .note(request.getNote() != null ? request.getNote() : "")
            .build();

        HabitCompletion savedCompletion = habitCompletionRepository.save(completion);
        return savedCompletion.getId();
    }

    @Transactional(readOnly = true)
    public List<HabitCompletionResponse> getCompletionsByHabit(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Verify ownership
        if (!habit.isOwnedBy(userId)) {
            throw new RuntimeException("Unauthorized access to habit");
        }

        return habitCompletionRepository.findByHabit(habit).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HabitCompletionResponse> getCompletionsByHabitAndDateRange(
        Long habitId, Long userId, Instant startDate, Instant endDate) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Verify ownership
        if (!habit.isOwnedBy(userId)) {
            throw new RuntimeException("Unauthorized access to habit");
        }

        return habitCompletionRepository.findByHabitAndCompletionDateBetween(habit, startDate, endDate).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private HabitCompletionResponse toResponse(HabitCompletion completion) {
        return HabitCompletionResponse.builder()
            .id(completion.getId())
            .habitId(completion.getHabit().getId())
            .completionDate(completion.getCompletionDate())
            .done(completion.isDone())
            .note(completion.getNote())
            .createdAt(completion.getCreatedAt())
            .updatedAt(completion.getUpdatedAt())
            .build();
    }

    @Transactional(readOnly = true)
    public HabitCompletion getCompletionById(Long completionId, Long userId) {
        HabitCompletion completion = habitCompletionRepository.findById(completionId)
            .orElseThrow(() -> new RuntimeException("Habit completion not found"));

        // Verify ownership through habit
        if (!completion.getHabit().isOwnedBy(userId)) {
            throw new RuntimeException("Unauthorized access to habit completion");
        }

        return completion;
    }

    @Transactional
    public void updateCompletion(Long completionId, Long userId, HabitCompletionRequest request) {
        HabitCompletion completion = getCompletionById(completionId, userId);

        // Create a new completion entity since we don't have setter methods
        HabitCompletion updatedCompletion = HabitCompletion.builder()
            .id(completion.getId())
            .habit(completion.getHabit())
            .completionDate(request.getCompletionDate())
            .done(request.getDone())
            .note(request.getNote() != null ? request.getNote() : "")
            .createdAt(completion.getCreatedAt())
            .build();

        habitCompletionRepository.save(updatedCompletion);
    }

    @Transactional
    public void deleteCompletion(Long completionId, Long userId) {
        HabitCompletion completion = getCompletionById(completionId, userId);
        habitCompletionRepository.delete(completion);
    }

    @Transactional(readOnly = true)
    public long getCompletionCount(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Verify ownership
        if (!habit.isOwnedBy(userId)) {
            throw new RuntimeException("Unauthorized access to habit");
        }

        return habitCompletionRepository.countByHabitAndDoneTrue(habit);
    }
}