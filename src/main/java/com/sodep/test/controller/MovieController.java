package com.sodep.test.controller;

import com.sodep.test.domain.ProcessingResult.BatchResult;
import com.sodep.test.service.MovieServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieServices movieService;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> sync(@RequestParam(required = false) Integer limit) {
        BatchResult result = movieService.sync(limit);
        return ResponseEntity.ok(Map.of(
                "processed", result.processedCount(),
                "skipped", result.skippedCount(),
                "failed", result.failedCount()
        ));
    }
}
