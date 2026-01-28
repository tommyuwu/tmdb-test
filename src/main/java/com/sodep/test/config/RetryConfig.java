package com.sodep.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate(TMDBApiProperties properties) {
        var backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10_000L);

        var retryPolicy = new SimpleRetryPolicy(
                properties.maxRetries(),
                Map.of(
                        HttpServerErrorException.class, true,
                        ResourceAccessException.class, true
                )
        );

        var template = new RetryTemplate();
        template.setBackOffPolicy(backOff);
        template.setRetryPolicy(retryPolicy);
        return template;
    }
}
