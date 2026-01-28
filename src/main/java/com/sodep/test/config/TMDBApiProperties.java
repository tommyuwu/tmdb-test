package com.sodep.test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.api")
public record TMDBApiProperties(
        String baseUrl,
        AuthType authType,
        String apiKey,
        String bearerToken,
        int connectTimeoutMs,
        int readTimeoutMs,
        int maxRetries
) {

    public enum AuthType { API_KEY, BEARER }

    public TMDBApiProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("external.api.base-url must not be blank");
        }
        if (authType == null) {
            authType = AuthType.API_KEY;
        }
        switch (authType) {
            case API_KEY -> {
                if (apiKey == null || apiKey.isBlank()) {
                    throw new IllegalArgumentException(
                            "external.api.api-key must not be blank when auth-type is API_KEY");
                }
            }
            case BEARER -> {
                if (bearerToken == null || bearerToken.isBlank()) {
                    throw new IllegalArgumentException(
                            "external.api.bearer-token must not be blank when auth-type is BEARER");
                }
            }
        }
        if (connectTimeoutMs <= 0) connectTimeoutMs = 5000;
        if (readTimeoutMs <= 0) readTimeoutMs = 10000;
        if (maxRetries < 0) maxRetries = 3;
    }
}
