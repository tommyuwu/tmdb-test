package com.sodep.test.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovieDto(
        int id,
        String title,
        @JsonProperty("original_language")
        String originalLanguage,
        String overview,
        Double popularity,
        @JsonProperty("vote_average")
        Double voteAverage,
        @JsonProperty("vote_count")
        Integer voteCount,
        @JsonProperty("release_date")
        String releaseDate,
        Boolean adult
) {}
