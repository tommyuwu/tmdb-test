package com.sodep.test.service;

import com.sodep.test.client.dto.MovieDto;
import com.sodep.test.domain.ProcessingResult;
import com.sodep.test.domain.ProcessingResult.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataProcessor {

    public ProcessingResult process(MovieDto movie) {
        try {
            String externalId = String.valueOf(movie.id());

            if (Boolean.TRUE.equals(movie.adult())) {
                return new Skipped(externalId, "Adult content");
            }
            if (movie.voteAverage() == null || movie.voteAverage() == 0) {
                return new Skipped(externalId, "No ratings yet");
            }

            return processMovie(movie, externalId);
        } catch (Exception e) {
            log.error("Error processing movie {}: {}", movie.id(), e.getMessage());
            return new Failed(String.valueOf(movie.id()), e.getMessage(), e);
        }
    }

    private Processed processMovie(MovieDto movie, String externalId) {
        String normalizedLanguage = normalizeLanguage(movie.originalLanguage());
        double computedValue = computeValue(movie.voteAverage(), normalizedLanguage);
        return new Processed(externalId, normalizedLanguage, computedValue);
    }

    private String normalizeLanguage(String language) {
        if (language == null) return "UNKNOWN";
        return language.trim().toUpperCase();
    }

    private double computeValue(double voteAverage, String language) {
        return switch (language) {
            case "EN" -> voteAverage * 1.15;
            case "ES", "FR", "DE", "IT", "PT" -> voteAverage * 1.0;
            default -> voteAverage * 0.85;
        };
    }
}
