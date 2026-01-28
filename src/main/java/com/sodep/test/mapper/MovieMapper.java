package com.sodep.test.mapper;

import com.sodep.test.client.dto.MovieDto;
import com.sodep.test.domain.ProcessingResult;
import com.sodep.test.domain.SyncStatus;
import com.sodep.test.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieDto dto, ProcessingResult.Processed result) {
        return Movie.builder()
                .externalId(String.valueOf(dto.id()))
                .name(dto.title())
                .category(result.category())
                .originalValue(orZero(dto.voteAverage()))
                .computedValue(result.computedValue())
                .status(SyncStatus.SYNCED)
                .build();
    }

    public void updateEntity(Movie entity, MovieDto dto, ProcessingResult.Processed result) {
        entity.setName(dto.title());
        entity.setCategory(result.category());
        entity.setOriginalValue(orZero(dto.voteAverage()));
        entity.setComputedValue(result.computedValue());
        entity.setStatus(SyncStatus.SYNCED);
        entity.setFailureReason(null);
    }

    private static double orZero(Double value) {
        return value != null ? value : 0.0;
    }
}
