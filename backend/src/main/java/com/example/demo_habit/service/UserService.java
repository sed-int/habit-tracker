package com.example.demo_habit.service;

import com.example.demo_habit.domain.User;
import com.example.demo_habit.dto.UserRequest;
import com.example.demo_habit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // private final MailClient

    @Transactional
    public Long register(UserRequest request) {
        // check if email is already registered
        checkDuplicatedUser(request.getEmail());
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(hashedPassword)
            .active(false)
            .admin(false)
            .build();
        User savedUser = userRepository.save(user);
        // send confirmation mail with link
        return savedUser.getId();
    }

    public boolean confirmRegistration(String otp) {
        // check timeout
        return true;
    }

    // helper method to see if user with the email exists
    private void checkDuplicatedUser(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
