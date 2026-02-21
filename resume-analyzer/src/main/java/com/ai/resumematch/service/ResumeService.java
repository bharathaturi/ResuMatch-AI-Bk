package com.ai.resumematch.service;

import com.ai.resumematch.dto.AnalysisResponse;
import com.ai.resumematch.model.AnalysisResult;
import com.ai.resumematch.model.Resume;
import com.ai.resumematch.model.User;
import com.ai.resumematch.repository.AnalysisRepository;
import com.ai.resumematch.repository.ResumeRepository;
import com.ai.resumematch.repository.UserRepository;
import com.ai.resumematch.config.JwtUtil;
import com.ai.resumematch.util.PdfParserUtil;
import com.ai.resumematch.util.SkillExtractorUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ResumeService {

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PdfParserUtil pdfParser;

    @Autowired
    private SkillExtractorUtil skillExtractor;

    @Autowired
    private AiService aiService;

    /**
     * Corrected analyze method to match the controller's call.
     * Signature: (MultipartFile, String, String)
     */
    public AnalysisResponse analyze(MultipartFile file, String jobDescription, String token)
            throws IOException, TikaException {
        // 1. Extract Text from PDF
        String resumeText = pdfParser.parse(file);

        // We still extract text out of the PDF. We can optionally extract keyword text,
        // but now OpenAI is doing the heavy lifting matching JD to resume text.
        // List<String> matchedSkills = skillExtractor.extract(resumeText);

        // 3. Get Deep AI Analysis from OpenAI (returns structured JSON string)
        String aiFeedbackJson = aiService.getDetailedAiAnalysis(resumeText, jobDescription);

        // 4. Parse the AI JSON Response
        AnalysisResponse response;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            response = mapper.readValue(aiFeedbackJson, AnalysisResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback in case OpenAI returns malformed JSON despite instructions
            response = new AnalysisResponse();
            response.setScore(0);
            response.setAiDetailedAnalysis("Error parsing AI response: " + aiFeedbackJson);
        }

        // 5. Ensure breakdown exists
        response.setBreakdown(new AnalysisResponse.ScoreBreakdown());

        // 6. Save Analysis to Database
        try {
            String jwt = token;
            if (token != null && token.startsWith("Bearer ")) {
                jwt = token.substring(7);
            }

            String email = jwtUtil.extractUsername(jwt);
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                Resume record = new Resume();
                record.setUser(user);
                record.setFileName(file.getOriginalFilename());

                AnalysisResult dbResult = new AnalysisResult();
                dbResult.setResume(record);
                dbResult.setMatchScore(response.getScore());
                dbResult.setAtsScore(response.getAtsScore());
                dbResult.setMatchingSkills(
                        response.getMatchSkills() != null ? String.join(", ", response.getMatchSkills()) : "");
                dbResult.setMissingSkills(
                        response.getMissingSkills() != null ? String.join(", ", response.getMissingSkills()) : "");
                dbResult.setSuggestions(
                        response.getSuggestions() != null ? String.join(", ", response.getSuggestions()) : "");
                dbResult.setAiDetailedAnalysis(response.getAiDetailedAnalysis());

                record.setAnalysisResult(dbResult);
                resumeRepository.save(record);
            }
        } catch (Exception e) {
            System.err.println("Failed to save resume history: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public List<Resume> getUserHistory(String token) {
        try {
            String jwt = token;
            if (token != null && token.startsWith("Bearer ")) {
                jwt = token.substring(7);
            }
            String email = jwtUtil.extractUsername(jwt);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                return resumeRepository.findByUserId(user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * Delete a specific resume history record.
     * Verifies that the resume belongs to the authenticated user.
     */
    public boolean deleteHistory(Long resumeId, String token) {
        try {
            String jwt = token;
            if (token != null && token.startsWith("Bearer ")) {
                jwt = token.substring(7);
            }
            String email = jwtUtil.extractUsername(jwt);
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                Resume resume = resumeRepository.findById(resumeId).orElse(null);
                // Security check: ensure the resume exists and belongs to the requesting user
                if (resume != null && resume.getUser().getId().equals(user.getId())) {
                    resumeRepository.delete(resume);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to delete resume history: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String rewrite(MultipartFile file, String jobDescription) throws IOException, TikaException {
        // 1. Extract plain text from PDF or DOCX
        String resumeText = pdfParser.parse(file);

        // 2. Pass text to Llama 3 to act as an expert resume writer
        return aiService.rewriteResume(resumeText, jobDescription);
    }
}