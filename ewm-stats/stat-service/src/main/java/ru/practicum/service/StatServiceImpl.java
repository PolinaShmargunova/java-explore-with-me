package ru.practicum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Override
    @Transactional
    public EndpointHitDto saveStat(EndpointHitDto dto) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.convertValue(
                statRepository.save(mapper.convertValue(dto, EndpointHit.class)), EndpointHitDto.class);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("параметры запроса дат не верны");
        }
        if (uris == null || uris.isEmpty()) {
            return new ArrayList<>(statRepository.getStatsAll(start, end));
        }
        if (unique) {
            return new ArrayList<>(statRepository.getStatsUnique(start, end, uris));
        } else {
            return new ArrayList<>(statRepository.getStats(start, end, uris));
        }
    }
}
