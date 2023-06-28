package ru.practicum.explorewithme.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatClient;
import ru.practicum.explorewithme.model.EventSortOption;
import ru.practicum.explorewithme.model.event.EventFullDto;
import ru.practicum.explorewithme.model.event.EventShortDto;
import ru.practicum.explorewithme.model.exception.ConvertationException;
import ru.practicum.explorewithme.service.event.EventService;
import ru.practicum.explorewithme.utils.CommonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicEventController {
    final EventService eventService;
    final StatClient client;

    @Autowired
    public PublicEventController(EventService eventService,
                                 @Value("${STATS_SERVER_URL:http://localhost:9090}") String serverUrl) {
        this.eventService = eventService;
        this.client = new StatClient(serverUrl);
    }

    @GetMapping
    public List<EventShortDto> getPublishedEvents(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                      LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                      LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_FROM)
                                                      @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_SIZE)
                                                      @Positive Integer size,
                                                  HttpServletRequest request) {
        log.trace("Поиск опубликованного события: текст = '{}', категории {}, платные {} за период {}-{} только доступные {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        EventSortOption sortOption = null;
        if (sort != null) {
            sortOption = EventSortOption.from(sort).orElseThrow(() -> {
                throw new ConvertationException("Не удалось найти EventSortOption " + sort);
            });
        }
        List<EventShortDto> result = eventService.getPublishedEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, from, size, sortOption);
        client.addHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()).block();
        return result;
    }

    @GetMapping("/{id}")
    public EventFullDto getPublishedEventById(@PathVariable Long id, HttpServletRequest request) {
        log.trace("Запрос опубликованного события {}", id);
        EventFullDto result = eventService.getPublishedEventById(id);
        client.addHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()).block();
        return result;
    }
}
