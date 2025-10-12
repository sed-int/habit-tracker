package com.example.demo_habit.repository;

import com.example.demo_habit.domain.Habit;
import com.example.demo_habit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user); // returns empty list if none found
}
