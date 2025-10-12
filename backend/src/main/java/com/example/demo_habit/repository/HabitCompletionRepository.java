package com.example.demo_habit.repository;

import com.example.demo_habit.domain.Habit;
import com.example.demo_habit.domain.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    List<HabitCompletion> findByHabit(Habit habit);
    List<HabitCompletion> findByHabitAndCompletionDateBetween(Habit habit, Instant startDate, Instant endDate);
    Optional<HabitCompletion> findByHabitAndCompletionDate(Habit habit, Instant completionDate);
    long countByHabitAndDoneTrue(Habit habit);
}
