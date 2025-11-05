package com.example.demo_habit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitCompletionResponse {
    private Long id;
    private Long habitId;
    private LocalDateTime completionDate;
    private boolean done;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}