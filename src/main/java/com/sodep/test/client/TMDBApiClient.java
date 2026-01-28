package com.sodep.test.client;

import com.sodep.test.client.dto.MoviePageResponse;

public interface TMDBApiClient {

    MoviePageResponse fetchPage(int page);
}
