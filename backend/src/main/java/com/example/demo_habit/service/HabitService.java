package com.example.demo_habit.service;

import com.example.demo_habit.domain.Habit;
import com.example.demo_habit.domain.User;
import com.example.demo_habit.dto.HabitRequest;
import com.example.demo_habit.repository.HabitRepository;
import com.example.demo_habit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"));

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
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"));
        return habitRepository.findByUserAndStatus(user, "ACTIVE");
    }

    @Transactional(readOnly = true)
    public Habit getHabitById(Long habitId, Long userId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Habit not found"));

        // Verify ownership
        if (!habit.isOwnedBy(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Unauthorized access to habit");
        }

        return habit;
    }

    @Transactional
    public void updateHabit(Long habitId, Long userId, HabitRequest request) {
        Habit habit = getHabitById(habitId, userId);
        habit.update(
            request.getTitle(),
            request.getDescription(),
            request.getTags(),
            request.getStar(),
            request.getPeriodType(),
            request.getPeriodCount(),
            request.getTargetCount()
        );
    }

    @Transactional
    public void deleteHabit(Long habitId, Long userId) {
        Habit habit = getHabitById(habitId, userId);
        habit.softDelete();
    }

    @Transactional
    public void archiveHabit(Long habitId, Long userId) {
        Habit habit = getHabitById(habitId, userId);
        habit.archive();
    }
}
