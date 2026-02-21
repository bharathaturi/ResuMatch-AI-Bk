package com.ai.resumematch.util;
import org.springframework.stereotype.Component;
import java.util.*;
        import java.util.stream.Collectors;

@Component
public class SkillExtractorUtil {

    private static final List<String> TECH_SKILLS = Arrays.asList(
            "Java", "Python", "React", "Spring Boot", "AWS", "SQL", "Docker",
            "Kubernetes", "JavaScript", "TypeScript", "Node.js", "Tailwind CSS"
    );

    public List<String> extract(String text) {
        return TECH_SKILLS.stream()
                .filter(skill -> text.toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }
}