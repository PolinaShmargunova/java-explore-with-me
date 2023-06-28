package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    @Transactional
    EndpointHitDto saveStat(EndpointHitDto dto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}