package com.sodep.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient externalApiRestClient(TMDBApiProperties properties) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.connectTimeoutMs());
        factory.setReadTimeout(properties.readTimeoutMs());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(authInterceptor(properties))
                .build();
    }

    private ClientHttpRequestInterceptor authInterceptor(TMDBApiProperties properties) {
        return (request, body, execution) -> {
            switch (properties.authType()) {
                case API_KEY -> request.getHeaders().set("X-API-Key", properties.apiKey());
                case BEARER -> request.getHeaders().set(
                        HttpHeaders.AUTHORIZATION, "Bearer " + properties.bearerToken());
            }
            return execution.execute(request, body);
        };
    }
}
