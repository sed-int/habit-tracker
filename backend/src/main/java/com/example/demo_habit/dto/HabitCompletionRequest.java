package com.example.demo_habit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitCompletionRequest {
    @NotNull(message = "Habit ID is required")
    private Long habitId;

    @NotNull(message = "Completion date is required")
    private LocalDateTime completionDate;

    @NotNull(message = "Done status is required")
    private Boolean done;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
}
