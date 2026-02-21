package com.ai.resumematch.service;

import com.ai.resumematch.config.JwtUtil;
import com.ai.resumematch.model.User;
import com.ai.resumematch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registers a new user with an encoded password.
     */
    public Map<String, Object> register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }
        // Corrected: Use getPassword() instead of password()
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return Map.of(
                "token", token,
                "user", Map.of("name", savedUser.getName(), "email", savedUser.getEmail()));
    }

    /**
     * Validates user credentials and returns a JWT token.
     */
    public Map<String, Object> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Corrected: Use getPassword() instead of password()
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            User user = userOptional.get();
            String token = jwtUtil.generateToken(email);
            return Map.of(
                    "token", token,
                    "user", Map.of("name", user.getName(), "email", user.getEmail()));
        }

        throw new RuntimeException("Invalid Credentials");
    }
}