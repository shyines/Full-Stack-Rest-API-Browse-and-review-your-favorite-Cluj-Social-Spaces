package com.studentreview.server.service;

import com.studentreview.server.dto.AuthResponse;
import com.studentreview.server.dto.LoginRequest;
import com.studentreview.server.dto.RegisterRequest;
import com.studentreview.server.dto.UserResponse;
import com.studentreview.server.model.User;
import com.studentreview.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword()); 
        
        try {
            user.setRole(User.UserRole.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            user.setRole(User.UserRole.GUEST);
        }

        if (user.getRole() == User.UserRole.OWNER) {
            user.setBusinessName(request.getBusinessName());
        }

        user = userRepository.save(user);

        return new AuthResponse("Registration successful", convertToUserResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        return new AuthResponse("Login successful", convertToUserResponse(user));
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getBusinessName(),
                user.getIsActive()
        );
    }
}
