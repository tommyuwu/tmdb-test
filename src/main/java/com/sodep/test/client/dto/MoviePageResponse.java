package com.sodep.test.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MoviePageResponse(
        List<MovieDto> results,
        Integer page,
        @JsonProperty("total_pages")
        Integer totalPages,
        @JsonProperty("total_results")
        Long totalResults
) {
    public boolean hasNext() {
        return page < totalPages;
    }
}
