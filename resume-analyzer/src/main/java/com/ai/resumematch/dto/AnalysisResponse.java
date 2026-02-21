package com.ai.resumematch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

/**
 * Data Transfer Object for the Resume Analysis results.
 * Using explicit annotations instead of @Data to prevent constructor conflicts.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnalysisResponse {
    private int score;
    private int atsScore;
    private List<String> matchSkills;
    private List<String> missingSkills;
    private List<String> suggestions;
    private String aiDetailedAnalysis;
    private ScoreBreakdown breakdown;

    public void setAiDetailedAnalysis(String aiDetailedAnalysis) {
        this.aiDetailedAnalysis = aiDetailedAnalysis;
    }

    public void setBreakdown(ScoreBreakdown scoreBreakdown) {
        this.breakdown = scoreBreakdown;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public void setMatchSkills(List<String> matchSkills) {
        this.matchSkills = matchSkills;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Inner class for the detailed score metrics.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ScoreBreakdown {
        private int skills;
        private int experience;
        private int education;
    }
}