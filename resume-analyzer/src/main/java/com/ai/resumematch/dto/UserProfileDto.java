package com.ai.resumematch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String name;
    private String email;
    private String phone;
    private String currentRole;
    private Integer experienceYears;
    private String linkedinUrl;
    private String portfolioUrl;
}
