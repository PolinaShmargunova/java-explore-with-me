package ru.practicum;

import lombok.Data;

@Data
public class ViewStatsDto {
    private String app;
    private String uri;
    private Integer hist;
}
