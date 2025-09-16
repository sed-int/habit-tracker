package com.example.demo_habit.dto;


import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class UserRequest {
    // id and many others
    private String email;
    private String password;
}
