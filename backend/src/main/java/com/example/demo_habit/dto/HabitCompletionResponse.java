package com.example.demo_habit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitCompletionResponse {
    private Long id;
    private Long habitId;
    private Instant completionDate;
    private boolean done;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
}