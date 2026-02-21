package com.ai.resumematch.controller;

import com.ai.resumematch.dto.UserProfileDto;
import com.ai.resumematch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000") // React port
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@RequestHeader("Authorization") String token) {
        try {
            UserProfileDto profile = userService.getUserProfile(token);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserProfileDto profileDto) {
        try {
            UserProfileDto updatedProfile = userService.updateUserProfile(token, profileDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
