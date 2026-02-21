package com.ai.resumematch.controller;

import com.ai.resumematch.dto.AnalysisResponse;
import com.ai.resumematch.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for Resume Analysis endpoints.
 */
@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:3000") // React port
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    /**
     * Endpoint to upload and analyze a resume against a JD.
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription,
            @RequestHeader("Authorization") String token) {

        // 1. Validate file
        if (file.isEmpty()) {
            System.out.println("Validation failed: file is empty");
            return ResponseEntity.badRequest().build();
        }

        String contentType = file.getContentType();
        if (contentType == null
                || (!contentType.equals("application/pdf")
                        && !contentType.equals("application/x-pdf")
                        && !contentType.equals("application/msword")
                        && !contentType
                                .equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            System.out.println("Validation failed: invalid content type. Received: " + contentType);
            return ResponseEntity.badRequest().build();
        }

        try {
            // 2. Extract Text & Process
            AnalysisResponse result = resumeService.analyze(file, jobDescription, token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            // Return 500 without hitting the generic /error mapping which might be secured
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Fetch user history.
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(resumeService.getUserHistory(token));
    }

    /**
     * Delete a specific history record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        boolean isDeleted = resumeService.deleteHistory(id, token);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            // Return Forbidden if the resume doesn't belong to the user, or NotFound if it
            // doesn't exist
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * Endpoint to use AI to rewrite a user's resume according to a Job Description
     */
    @PostMapping("/rewrite")
    public ResponseEntity<String> rewriteResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String rewrittenText = resumeService.rewrite(file, jobDescription);
            return ResponseEntity.ok(rewrittenText);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to process resume rewriting");
        }
    }
}