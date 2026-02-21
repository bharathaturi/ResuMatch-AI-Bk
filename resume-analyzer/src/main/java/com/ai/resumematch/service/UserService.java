package com.ai.resumematch.service;

import com.ai.resumematch.config.JwtUtil;
import com.ai.resumematch.dto.UserProfileDto;
import com.ai.resumematch.model.User;
import com.ai.resumematch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public UserProfileDto getUserProfile(String token) {
        String jwt = token;
        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        }
        String email = jwtUtil.extractUsername(jwt);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new UserProfileDto(
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getCurrentRole(),
                    user.getExperienceYears(),
                    user.getLinkedinUrl(),
                    user.getPortfolioUrl());
        }
        throw new RuntimeException("User not found");
    }

    public UserProfileDto updateUserProfile(String token, UserProfileDto dto) {
        String jwt = token;
        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        }
        String email = jwtUtil.extractUsername(jwt);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Update fields except email and password
            user.setName(dto.getName() != null ? dto.getName() : user.getName());
            user.setPhone(dto.getPhone());
            user.setCurrentRole(dto.getCurrentRole());
            user.setExperienceYears(dto.getExperienceYears());
            user.setLinkedinUrl(dto.getLinkedinUrl());
            user.setPortfolioUrl(dto.getPortfolioUrl());

            User savedUser = userRepository.save(user);

            return new UserProfileDto(
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getPhone(),
                    savedUser.getCurrentRole(),
                    savedUser.getExperienceYears(),
                    savedUser.getLinkedinUrl(),
                    savedUser.getPortfolioUrl());
        }
        throw new RuntimeException("User not found");
    }
}
