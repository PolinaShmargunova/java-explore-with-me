package ru.practicum.explorewithme.service.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.model.EventSortOption;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventFullDto;
import ru.practicum.explorewithme.model.event.EventShortDto;
import ru.practicum.explorewithme.model.event.NewEventDto;
import ru.practicum.explorewithme.model.event.UpdateEventAdminRequest;
import ru.practicum.explorewithme.model.event.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEvents(List<Long> users,
                                 List<String> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Pageable pageable);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest adminRequest);

    List<EventShortDto> getEventsOfUser(Long userId, Pageable pageable);

    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest userRequest);

    List<EventShortDto> getPublishedEvents(String text,
                                           List<Long> categories,
                                           Boolean paid,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           Boolean onlyAvailable,
                                           Integer from,
                                           Integer size,
                                           EventSortOption sortOption);

    List<EventShortDto> getPublishedEventsOfUsers(List<Long> userIds,
                                                  String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  Integer from,
                                                  Integer size,
                                                  EventSortOption sortOption);

    EventFullDto getPublishedEventById(Long id);

    Event getEventById(Long eventId);
}
