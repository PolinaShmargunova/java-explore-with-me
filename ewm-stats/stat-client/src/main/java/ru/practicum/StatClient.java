package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatClient {
    private static final String SERVER_URL = "http://localhost:9090";

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient client;

    public StatClient() {
        this.client = WebClient.builder()
                .baseUrl(SERVER_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<EndpointHitDto> addHit(EndpointHitDto dto) {
        return client
                .post()
                .uri("/hit")
                .body(dto, EndpointHitDto.class)
                .retrieve()
                .bodyToMono(EndpointHitDto.class);
    }

    public Mono<List<ViewStatsDto>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startString = start.format(DTF);
        String endString = end.format(DTF);
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", startString)
                        .queryParam("end", endString)
                        .queryParam("unique", unique)
                        .queryParam("uris", uris)
                        .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}
