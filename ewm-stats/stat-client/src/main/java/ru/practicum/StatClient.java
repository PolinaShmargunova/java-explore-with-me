package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
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

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final WebClient client;

    public StatClient(@Value("${STATS_SERVER_URL}") String serverUrl) {
        this.client = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<EndpointHitDto> addHit(String app, String uri, String ip, LocalDateTime timestamp) {
        return client
                .post()
                .uri("/hit")
                .bodyValue(new EndpointHitDto(null, app, uri, ip, timestamp))
                .retrieve()
                .bodyToMono(EndpointHitDto.class);
    }

    public Mono<List<ViewStats>> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
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
