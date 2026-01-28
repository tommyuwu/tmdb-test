package com.sodep.test.client;

import com.sodep.test.client.dto.MoviePageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class TMDBApiClientImpl implements TMDBApiClient {

    private final RestClient restClient;
    private final RetryTemplate retryTemplate;

    @Override
    public MoviePageResponse fetchPage(int page) {
        return retryTemplate.execute(context -> {
            if (context.getRetryCount() > 0) {
                log.warn("Retry attempt {} for page {}", context.getRetryCount(), page);
            }
            return restClient.get()
                    .uri("/3/movie/popular?page={page}", page)
                    .retrieve()
                    .body(MoviePageResponse.class);
        });
    }
}
