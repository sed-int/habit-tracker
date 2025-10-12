package com.example.demo_habit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private String tags;  // Comma-separated tags

    private Boolean star;  // Favorite flag (default: false)

    private Character periodType;  // 'D' for daily, 'W' for weekly, etc.

    private Long periodCount;  // Number of periods

    private Long targetCount;  // Target completion count
}