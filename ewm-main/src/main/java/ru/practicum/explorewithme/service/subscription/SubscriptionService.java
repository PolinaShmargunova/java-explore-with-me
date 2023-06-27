package ru.practicum.explorewithme.service.subscription;

import ru.practicum.explorewithme.model.EventSortOption;
import ru.practicum.explorewithme.model.event.EventShortDto;
import ru.practicum.explorewithme.model.subscription.SubscriptionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionService {
    SubscriptionDto createSubscription(Long userId, Long subscriberId);

    void removeSubscription(Long userId, Long subscriberId);

    List<EventShortDto> getEventsFromSubscriptions(Long userId, String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                                   Integer from, Integer size, EventSortOption sortOption);

    List<SubscriptionDto> getSubscriptions(Long userId);
}
