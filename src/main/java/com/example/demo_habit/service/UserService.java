package com.example.demo_habit.service;

import com.example.demo_habit.domain.User;
import com.example.demo_habit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Long register(String email, String password) {
        // check if email is already registered
        checkDuplicatedUser(email);
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(email, hashedPassword);
        userRepository.save(user);
        return user.getId();
    }

    // helper method to see if user with the email exists
    private void checkDuplicatedUser(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Duplicated user");
        }
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
}
