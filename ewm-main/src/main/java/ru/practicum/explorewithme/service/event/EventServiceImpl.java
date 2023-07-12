package ru.practicum.explorewithme.service.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.ViewStats;
import ru.practicum.explorewithme.model.EventSortOption;
import ru.practicum.explorewithme.model.category.Category;
import ru.practicum.explorewithme.model.event.*;
import ru.practicum.explorewithme.model.exception.AdminUpdateStatusException;
import ru.practicum.explorewithme.model.exception.BadRequestException;
import ru.practicum.explorewithme.model.exception.ObjectNotFoundException;
import ru.practicum.explorewithme.model.exception.UserUpdateStatusException;
import ru.practicum.explorewithme.model.request.QRequest;
import ru.practicum.explorewithme.model.request.Request;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.LocationRepository;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatClient client;
    private final EventMapper mapper;
    private final RequestRepository requestRepository;
    private BooleanBuilder booleanBuilder = new BooleanBuilder(QEvent.event.state.eq(EventState.PUBLISHED));

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                            CategoryRepository categoryRepository, LocationRepository locationRepository,
                            EventMapper mapper, RequestRepository requestRepository,
                            @Value("${STATS_SERVER_URL:http://localhost:9090}") String serverUrl) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.mapper = mapper;
        this.requestRepository = requestRepository;
        this.client = new StatClient(serverUrl);
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users,
                                        List<String> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Pageable pageable) {
        if (users != null && !users.isEmpty()) {
            booleanBuilder.and(QEvent.event.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            List<EventState> eventStates = states.stream().map(state -> EventState.from(state).orElseThrow(() ->
                    new RuntimeException("Не удалось найти статус " + state))).collect(Collectors.toList());
            booleanBuilder.and(QEvent.event.state.in(eventStates));
        }
        if (categories != null && !categories.isEmpty()) {
            booleanBuilder.and(QEvent.event.category.id.in(categories));
        }
        if (rangeStart != null && rangeEnd != null) {
            booleanBuilder.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
        }
        List<Event> events = eventRepository.findAll(booleanBuilder, pageable).getContent();
        setConfirmedRequests(events);
        setViews(events);
        return events.stream().map(mapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest adminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найдено событие с id " + eventId));
        checkRequestAnnotation(eventId, event, adminRequest.getAnnotation(), adminRequest.getCategory(), adminRequest.getDescription());
        if (adminRequest.getEventDate() != null) {
            event.setEventDate(adminRequest.getEventDate());
        }
        if (adminRequest.getEventDate() != null && adminRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Изменение даты события на уже наступившую невозможно");
        }
        checkRequestLocation(event, adminRequest.getLocation(), adminRequest.getPaid(),
                adminRequest.getParticipantLimit(), adminRequest.getRequestModeration(), adminRequest.getTitle());
        if (adminRequest.getStateAction() != null) {
            switch (adminRequest.getStateAction()) {
                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new AdminUpdateStatusException("Нельзя отменить опубликованное событие");
                    }
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    if (event.getState() != EventState.PENDING) {
                        throw new AdminUpdateStatusException("Нельзя опубликовать событие, которое не в  статусе PENDING");
                    }
                    event.setState(EventState.PUBLISHED);
            }
        }
        eventRepository.save(event);
        return mapper.toEventFullDto(setConfirmedRequestAndViews(event));
    }

    @Override
    public List<EventShortDto> getEventsOfUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        List<Event> events = setConfirmedRequestsAndViews(eventRepository.findByInitiator(user, pageable).getContent());
        return events.stream().map(mapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        if (eventDto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата события не может быть в прошлом");
        }
        Event event = this.toEvent(eventDto);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setRequestModeration(true);
        return mapper.toEventFullDto(eventRepository.save(event));
    }

    private Event toEvent(NewEventDto dto) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());
        event.setCategory(categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new ObjectNotFoundException("Не найдена категория с id " + dto.getCategory())));
        LocationDto locationDto = dto.getLocation();
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon()).orElse(
                locationRepository.save(new Location(locationDto.getLat(), locationDto.getLon()))
        );
        event.setLocation(location);
        return event;
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        Event event = eventRepository.findByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new ObjectNotFoundException("Не найдено событие с id " + eventId));
        return mapper.toEventFullDto(setConfirmedRequestAndViews(event));
    }

    @Override
    @Transactional
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest userRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        Event event = eventRepository.findByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new ObjectNotFoundException("Не найдено событие с id " + eventId));
        if (event.getState() != EventState.CANCELED && event.getState() != EventState.PENDING) {
            throw new UserUpdateStatusException("Изменить статус можно только из статусов PENDING и CANCELED");
        }
        checkRequestAnnotation(eventId, event, userRequest.getAnnotation(), userRequest.getCategory(), userRequest.getDescription());
        if (userRequest.getEventDate() != null && userRequest.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата изменения события не может быть в прошлом");
        }
        if (userRequest.getEventDate() != null) {
            event.setEventDate(userRequest.getEventDate());
        }
        checkRequestLocation(event, userRequest.getLocation(), userRequest.getPaid(),
                userRequest.getParticipantLimit(), userRequest.getRequestModeration(),
                userRequest.getTitle());

        if (userRequest.getStateAction() != null) {
            switch (userRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
            }
        }
        return mapper.toEventFullDto(setConfirmedRequestAndViews(eventRepository.save(event)));
    }

    @Override
    public List<EventShortDto> getPublishedEvents(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  Integer from,
                                                  Integer size,
                                                  EventSortOption sortOption) {
        checkRequestText(text, categories, paid, rangeStart, rangeEnd);
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Даты поиска событий не верны");
        }
        List<EventShortDto> events = checkRequestAvailable(onlyAvailable, from, size, sortOption);
        return events.stream().collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublishedEventById(Long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new ObjectNotFoundException("Не найдено событие с id " + id));
        return mapper.toEventFullDto(setConfirmedRequestAndViews(event));
    }

    @Override
    public List<EventShortDto> getPublishedEventsOfUsers(List<Long> userIds,
                                                         String text,
                                                         List<Long> categories,
                                                         Boolean paid,
                                                         LocalDateTime rangeStart,
                                                         LocalDateTime rangeEnd,
                                                         Boolean onlyAvailable,
                                                         Integer from,
                                                         Integer size,
                                                         EventSortOption sortOption) {
        if (userIds != null && !userIds.isEmpty()) {
            booleanBuilder.and(QEvent.event.initiator.id.in(userIds));
        }
        checkRequestText(text, categories, paid, rangeStart, rangeEnd);
        List<EventShortDto> events = checkRequestAvailable(onlyAvailable, from, size, sortOption);
        return events.stream().collect(Collectors.toList());
    }

    @Override
    public Event getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найдено событие с id " + eventId));
        return setConfirmedRequestAndViews(event);
    }

    private Event setConfirmedRequestAndViews(Event event) {
        return setConfirmedRequestsAndViews(Collections.singletonList(event)).stream().findFirst().orElseThrow(() -> {
            throw new RuntimeException("Ошибка получения данных о просмотрах и/или запросах");
        });
    }

    private List<Event> setConfirmedRequestsAndViews(Collection<Event> events) {
        return setViews(setConfirmedRequests(events));
    }

    private List<Event> setConfirmedRequests(Collection<Event> events) {
        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        }
        List<Request> requests = requestRepository.findByEventIdIn(events.stream()
                .map(Event::getId).collect(Collectors.toList()));
        return events.stream()
                .peek(event -> event.setConfirmedRequests(
                        requests.stream()
                                .filter(request -> request.getEvent().equals(event))
                                .count()))
                .collect(Collectors.toList());
    }

    private List<Event> setViews(Collection<Event> events) {
        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        }
        LocalDateTime from = events.stream()
                .min(Comparator.comparing(Event::getCreatedOn))
                .orElseThrow(() -> new RuntimeException("Событие без времени создания!"))
                .getCreatedOn();
        Map<Long, Long> views = getViews(events.stream().map(Event::getId).collect(Collectors.toList()), from);
        return events.stream()
                .peek(event -> event.setViews(views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getViews(List<Long> ids, LocalDateTime from) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        List<ViewStats> stats = client.getStats(from,
                        LocalDateTime.now(),
                        ids.stream().map(id -> "/events/" + id).collect(Collectors.toList()),
                        true)
                .block();
        if (stats == null || stats.isEmpty()) {
            return new HashMap<>();
        }
        return stats.stream().collect(Collectors.toMap(
                stat -> Long.parseLong(stat.getUri().replace("/events/", "")),
                ViewStats::getHits
        ));
    }

    private void checkRequestLocation(Event event, LocationDto location2, Boolean paid, Integer participantLimit,
                                      Boolean requestModeration, String title) {
        if (location2 != null) {
            LocationDto locationDto = location2;
            Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon()).orElse(
                    locationRepository.save(new Location(locationDto.getLat(), locationDto.getLon()))
            );
            event.setLocation(location);
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }
    }

    private void checkRequestAnnotation(Long eventId, Event event, String annotation,
                                        Long category2, String description) {
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category2 != null) {
            Category category = categoryRepository.findById(category2)
                    .orElseThrow(() -> new ObjectNotFoundException("Не найдена категория с id " + eventId));
            event.setCategory(category);
        }
        if (description != null) {
            event.setDescription(description);
        }
    }

    private void checkRequestText(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd) {

        if (text != null && !text.isBlank()) {
            BooleanExpression byTextInAnnotation = QEvent.event.annotation.likeIgnoreCase("%" + text + "%");
            BooleanExpression byTextInDescription = QEvent.event.description.likeIgnoreCase("%" + text + "%");
            booleanBuilder.and(byTextInAnnotation.or(byTextInDescription));
        }
        if (categories != null && categories.size() != 0) {
            booleanBuilder.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            booleanBuilder.and(QEvent.event.paid.eq(paid));
        }
        if (rangeStart != null && rangeEnd != null) {
            booleanBuilder.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
        } else {
            booleanBuilder.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }
    }

    private List<EventShortDto> checkRequestAvailable(Boolean onlyAvailable,
                                                      Integer from,
                                                      Integer size,
                                                      EventSortOption sortOption) {
        if (onlyAvailable != null && onlyAvailable) {
            BooleanExpression withoutLimit = QEvent.event.participantLimit.eq(0);
            BooleanExpression withLimitAvailable = QEvent.event.participantLimit.gt(
                    JPAExpressions.select(QRequest.request.count())
                            .from(QRequest.request)
                            .where(QRequest.request.event.eq(QEvent.event))
            );
            booleanBuilder.and(withoutLimit.or(withLimitAvailable));
        }
        List<Event> events = new ArrayList<>();
        eventRepository.findAll(booleanBuilder).forEach(events::add);
        events = setConfirmedRequestsAndViews(events);
        if (sortOption != null) {
            switch (sortOption) {
                case EVENT_DATE:
                    events = events.stream().sorted(Comparator.comparing(Event::getEventDate))
                            .skip(from).limit(size).collect(Collectors.toList());
                    break;
                case VIEWS:
                    events = events.stream().sorted((e1, e2) -> -Long.compare(e1.getViews(), e2.getViews()))
                            .skip(from).limit(size).collect(Collectors.toList());
                    break;
                default:
                    events = events.stream().skip(from).limit(size).collect(Collectors.toList());
            }
        } else {
            events = events.stream().skip(from).limit(size).collect(Collectors.toList());
        }
        return events.stream().map(mapper::toEventShortDto).collect(Collectors.toList());
    }
}