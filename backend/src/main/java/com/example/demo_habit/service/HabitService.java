package com.example.demo_habit.service;

import com.example.demo_habit.domain.Habit;
import com.example.demo_habit.domain.User;
import com.example.demo_habit.dto.HabitRequest;
import com.example.demo_habit.repository.HabitRepository;
import com.example.demo_habit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HabitService {
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createHabit(Long userId, HabitRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Habit habit = Habit.builder()
            .user(user)
            .title(request.getTitle())
            .description(request.getDescription())
            .tags(request.getTags() != null ? request.getTags() : "")
            .star(request.getStar() != null ? request.getStar() : false)
            .status("ACTIVE")
            .periodType(request.getPeriodType())
            .periodCount(request.getPeriodCount() != null ? request.getPeriodCount() : 0)
            .targetCount(request.getTargetCount() != null ? request.getTargetCount() : 0)
            .orderIndex(0) // TODO: calculate max order index + 1
            .build();

        Habit savedHabit = habitRepository.save(habit);
        return savedHabit.getId();
    }

    @Transactional(readOnly = true)
    public List<Habit> getHabitsByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return habitRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Habit getHabitById(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Verify ownership
        if (!habit.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to habit");
        }

        return habit;
    }

    @Transactional
    public void updateHabit(Long habitId, Long userId, HabitRequest request) {
        Habit habit = getHabitById(habitId, userId);

        // TODO: Use builder or setter methods to update fields
        // Since Habit has no setters, we need to recreate or add update methods
        habitRepository.save(Habit.builder()
            .id(habit.getId())
            .user(habit.getUser())
            .title(request.getTitle())
            .description(request.getDescription())
            .tags(request.getTags() != null ? request.getTags() : "")
            .star(request.getStar() != null ? request.getStar() : false)
            .status(habit.getStatus())
            .periodType(request.getPeriodType())
            .periodCount(request.getPeriodCount() != null ? request.getPeriodCount() : 0)
            .targetCount(request.getTargetCount() != null ? request.getTargetCount() : 0)
            .orderIndex(habit.getOrderIndex())
            .createdAt(habit.getCreatedAt())
            .build());
    }

    @Transactional
    public void deleteHabit(Long habitId, Long userId) {
        Habit habit = getHabitById(habitId, userId);

        // Soft delete by updating status
        habitRepository.save(Habit.builder()
            .id(habit.getId())
            .user(habit.getUser())
            .title(habit.getTitle())
            .description(habit.getDescription())
            .tags(habit.getTags())
            .star(habit.isStar())
            .status("SOFT_DELETED")
            .periodType(habit.getPeriodType())
            .periodCount(habit.getPeriodCount())
            .targetCount(habit.getTargetCount())
            .orderIndex(habit.getOrderIndex())
            .createdAt(habit.getCreatedAt())
            .build());
    }

    @Transactional
    public void archiveHabit(Long habitId, Long userId) {
        Habit habit = getHabitById(habitId, userId);

        habitRepository.save(Habit.builder()
            .id(habit.getId())
            .user(habit.getUser())
            .title(habit.getTitle())
            .description(habit.getDescription())
            .tags(habit.getTags())
            .star(habit.isStar())
            .status("ARCHIVED")
            .periodType(habit.getPeriodType())
            .periodCount(habit.getPeriodCount())
            .targetCount(habit.getTargetCount())
            .orderIndex(habit.getOrderIndex())
            .createdAt(habit.getCreatedAt())
            .build());
    }
}
