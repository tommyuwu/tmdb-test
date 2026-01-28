package com.sodep.test.service;

import com.sodep.test.client.TMDBApiClient;
import com.sodep.test.client.dto.MovieDto;
import com.sodep.test.client.dto.MoviePageResponse;
import com.sodep.test.domain.ProcessingResult;
import com.sodep.test.domain.ProcessingResult.BatchResult;
import com.sodep.test.entity.Movie;
import com.sodep.test.exception.SyncException;
import com.sodep.test.mapper.MovieMapper;
import com.sodep.test.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServices {

    private final TMDBApiClient apiClient;
    private final DataProcessor processor;
    private final MovieMapper mapper;
    private final MovieRepository repository;

    public BatchResult sync(Integer limit) {
        log.info("Starting sync{}", limit != null ? " (limit=%d)".formatted(limit) : "");
        List<ProcessingResult> allResults = new ArrayList<>();
        int page = 1;
        boolean hasMore = true;

        while (hasMore) {
            try {
                MoviePageResponse response = apiClient.fetchPage(page);
                List<MovieDto> movies = response.results();

                if (limit != null) {
                    int remaining = limit - allResults.size();
                    movies = movies.subList(0, Math.min(movies.size(), remaining));
                }

                processMovies(movies, allResults);
                hasMore = response.hasNext() && (limit == null || allResults.size() < limit);
                page++;
            } catch (HttpClientErrorException.Unauthorized e) {
                throw new SyncException.ApiAuthenticationException(
                        "Authentication failed: %s".formatted(e.getMessage()));
            } catch (HttpClientErrorException.NotFound e) {
                log.warn("Resource not found on page {}: {}", page, e.getMessage());
                hasMore = false;
            } catch (HttpClientErrorException e) {
                log.warn("Client error ({}) on page {}: {}", e.getStatusCode(), page, e.getMessage());
                hasMore = false;
            } catch (HttpServerErrorException e) {
                throw new SyncException.ApiUnreachableException(
                        "Server error: %s".formatted(e.getMessage()), e);
            } catch (ResourceAccessException e) {
                throw new SyncException.ApiUnreachableException(
                        "Network error: %s".formatted(e.getMessage()), e);
            }
        }

        var batch = new BatchResult(allResults);
        log.info("Sync complete: processed={}, skipped={}, failed={}",
                batch.processedCount(), batch.skippedCount(), batch.failedCount());
        return batch;
    }

    @Transactional
    protected void processMovies(List<MovieDto> movies, List<ProcessingResult> results) {
        for (MovieDto movie : movies) {
            ProcessingResult result = processor.process(movie);
            results.add(result);

            if (result instanceof ProcessingResult.Processed p) {
                upsertRecord(movie, p);
            } else {
                log.debug("Skipped/failed movie {}: {}", movie.id(), result);
            }
        }
    }

    private void upsertRecord(MovieDto movie, ProcessingResult.Processed result) {
        String externalId = String.valueOf(movie.id());
        repository.findByExternalId(externalId)
                .ifPresentOrElse(
                        existing -> {
                            mapper.updateEntity(existing, movie, result);
                            repository.save(existing);
                        },
                        () -> {
                            Movie entity = mapper.toEntity(movie, result);
                            repository.save(entity);
                        }
                );
    }
}
