package com.ai.resumematch.repository;

import com.ai.resumematch.model.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisResult, Long> {
    Optional<AnalysisResult> findByResumeId(Long resumeId);
}