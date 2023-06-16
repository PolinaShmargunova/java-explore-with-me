package ru.practicum;

import lombok.Data;

@Data
public class EndpointHitDto {
    private Integer id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
