package com.ai.resumematch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

/**
 * Service to handle communication with the OpenAI API.
 */
@Service
public class AiService {

        @Value("${groq.api.key}")
        private String apiKey;

        // Using Groq's OpenAI-compatible endpoint and Llama 3.1 8B
        private final String OPENAI_API_URL = "https://api.groq.com/openai/v1/chat/completions";
        private final String OPENAI_MODEL = "llama-3.1-8b-instant";

        public String getDetailedAiAnalysis(String resumeText, String jobDescription) {
                RestTemplate restTemplate = new RestTemplate();

                // Construct a precise prompt for the AI to return JSON
                String prompt = String.format(
                                "You are an expert HR Recruiter and Career Coach. Analyze the following resume against the job description provided. "
                                                +
                                                "Return ONLY a valid JSON object with the following structure. Do not wrap it in markdown or ```json tags. "
                                                +
                                                "{\n" +
                                                "  \"score\": <integer from 0 to 100 representing the match percentage>,\n"
                                                +
                                                "  \"atsScore\": <integer from 0 to 100 representing how ATS-friendly and readable the resume format is>,\n"
                                                +
                                                "  \"matchSkills\": [<array of strings of matching skills found in the resume>],\n"
                                                +
                                                "  \"missingSkills\": [<array of strings of AT LEAST 3 critical skills missing from the resume but required in the JD. DO NOT leave this array empty>],\n"
                                                +
                                                "  \"suggestions\": [<array of strings of actionable suggestions to improve the resume>],\n"
                                                +
                                                "  \"aiDetailedAnalysis\": \"<A detailed paragraph explaining the overall strengths, gaps, and why this score was given. YOU MUST explicitly list the missing/required skills for this specific job role in this paragraph.>\"\n"
                                                +
                                                "}\n\n" +
                                                "RESUME TEXT:\n%s\n\n" +
                                                "JOB DESCRIPTION:\n%s",
                                resumeText, jobDescription);

                // Construct the OpenAI API request payload
                Map<String, Object> requestBody = Map.of(
                                "model", OPENAI_MODEL,
                                "response_format", Map.of("type", "json_object"),
                                "messages", List.of(
                                                Map.of(
                                                                "role", "user",
                                                                "content", prompt)));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey); // Adds Authorization: Bearer <token>

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                try {
                        // Check if API key is present
                        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_OPENAI_API_KEY")) {
                                return "{\"score\":0,\"atsScore\":0,\"matchSkills\":[],\"missingSkills\":[],\"suggestions\":[\"API Key missing\"],\"aiDetailedAnalysis\":\"API Key missing in properties\"}";
                        }

                        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);

                        // Navigate the OpenAI response JSON structure
                        List choices = (List) response.getBody().get("choices");
                        Map firstChoice = (Map) choices.get(0);
                        Map message = (Map) firstChoice.get("message");

                        return (String) message.get("content");
                } catch (Exception e) {
                        String safeErrorMessage = e.getMessage() != null
                                        ? e.getMessage().replace("\"", "\\\"").replace("\n", " ")
                                        : "Unknown error";
                        return "{\"score\":0,\"atsScore\":0,\"matchSkills\":[],\"missingSkills\":[],\"suggestions\":[\"Analysis failed\"],\"aiDetailedAnalysis\":\"Error: "
                                        + safeErrorMessage + "\"}";
                }
        }

        public String rewriteResume(String resumeText, String jobDescription) {
                RestTemplate restTemplate = new RestTemplate();

                String prompt = String.format(
                                "You are an expert Executive Resume Writer and Career Coach. I am providing you with my current resume and a target Job Description. "
                                                + "Your task is to REWRITE my resume to perfectly align with this specific job description. "
                                                + "Follow these rules explicitly:\n"
                                                + "1. DO NOT fabricate or invent experience, education, or jobs that I do not have.\n"
                                                + "2. DO rephrase, reorder, and emphasize my existing bullet points to highlight the skills exactly as requested in the JD.\n"
                                                + "3. Weave the keywords from the JD naturally into my experience bullet points.\n"
                                                + "4. Output the result in clean, professional Markdown formatting, ready to be saved as a .md file.\n"
                                                + "5. ONLY return the rewritten resume markdown text. Do not include any conversational filler like 'Here is your resume'.\n\n"
                                                + "RESUME TEXT:\n%s\n\n"
                                                + "JOB DESCRIPTION:\n%s",
                                resumeText, jobDescription);

                Map<String, Object> requestBody = Map.of(
                                "model", OPENAI_MODEL,
                                "messages", List.of(
                                                Map.of(
                                                                "role", "user",
                                                                "content", prompt)));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                try {
                        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_OPENAI_API_KEY")) {
                                return "Error: API Key missing in application.properties";
                        }
                        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);

                        List choices = (List) response.getBody().get("choices");
                        Map firstChoice = (Map) choices.get(0);
                        Map message = (Map) firstChoice.get("message");

                        return (String) message.get("content");
                } catch (Exception e) {
                        return "Error generating rewritten resume: " + e.getMessage();
                }
        }
}