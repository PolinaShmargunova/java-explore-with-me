package ru.practicum.explorewithme.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventState;
import ru.practicum.explorewithme.model.exception.FullEventException;
import ru.practicum.explorewithme.model.exception.ObjectNotFoundException;
import ru.practicum.explorewithme.model.exception.PermissionException;
import ru.practicum.explorewithme.model.exception.RequestCreationException;
import ru.practicum.explorewithme.model.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.model.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.model.request.ParticipationRequestDto;
import ru.practicum.explorewithme.model.request.Request;
import ru.practicum.explorewithme.model.request.RequestMapper;
import ru.practicum.explorewithme.model.request.RequestState;
import ru.practicum.explorewithme.model.request.RequestUserState;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final RequestMapper mapper;

    @Override
    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        return requestRepository.findByRequester(user).stream()
                .map(mapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        Event event = eventService.getEventById(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new RequestCreationException("Нельзя создать запрос на неопубликованное событие");
        }
        if (event.getInitiator().equals(user)) {
            throw new RequestCreationException("Инициатор не может оставить запрос на свое событие");
        }
        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(event.getParticipantLimit() == 0 ? RequestState.CONFIRMED : RequestState.PENDING);
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new RequestCreationException("Не осталось свободных мест в данном событии");
        }
        return mapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден запрос с id " + requestId));
        if (!request.getRequester().equals(user)) {
            throw new PermissionException("Запрос создан другим пользователем");
        }
        request.setStatus(RequestState.CANCELED);
        return mapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        Event event = eventService.getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new PermissionException("Событие создано другим пользователем");
        }
        return requestRepository.findByEvent(event).stream()
                .map(mapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchRequestsOfEvent(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        Event event = eventService.getEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new PermissionException("Событие создано другим пользователем");
        }
        if (request.getStatus() == RequestUserState.CONFIRMED && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new FullEventException("Не осталось свободных мест в данном событии");
        }
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        for (Long requestId : request.getRequestIds()) {
            Request req = requestRepository.findById(requestId)
                    .orElseThrow(() -> new ObjectNotFoundException("Не найден запрос с id " + requestId));
            switch (request.getStatus()) {
                case REJECTED:
                    req.setStatus(RequestState.REJECTED);
                    updateResult.addRejectedRequest(mapper.toParticipationRequestDto(req));
                    break;
                case CONFIRMED:
                    if (event.getParticipantLimit() == 0
                            || event.getParticipantLimit() > event.getConfirmedRequests()) {
                        req.setStatus(RequestState.CONFIRMED);
                        updateResult.addConfirmedRequest(mapper.toParticipationRequestDto(req));
                    } else {
                        req.setStatus(RequestState.REJECTED);
                        updateResult.addRejectedRequest(mapper.toParticipationRequestDto(req));
                    }
            }
            requestRepository.save(req);
        }
        return updateResult;
    }
}
