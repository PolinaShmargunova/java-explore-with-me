package ru.practicum.explorewithme.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.model.EventSortOption;
import ru.practicum.explorewithme.model.event.EventShortDto;
import ru.practicum.explorewithme.model.exception.ObjectNotFoundException;
import ru.practicum.explorewithme.model.subscription.Subscription;
import ru.practicum.explorewithme.model.subscription.SubscriptionDto;
import ru.practicum.explorewithme.model.subscription.SubscriptionMapper;
import ru.practicum.explorewithme.model.user.User;
import ru.practicum.explorewithme.repository.SubscriptionRepository;
import ru.practicum.explorewithme.repository.UserRepository;
import ru.practicum.explorewithme.service.event.EventService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EventService eventService;
    private final SubscriptionMapper mapper;

    @Override
    @Transactional
    public SubscriptionDto createSubscription(Long userId, Long subscriberId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + subscriberId));
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriber(subscriber);
        subscription.setSubscribedOn(LocalDateTime.now());
        return mapper.toSubscriptionDto(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional
    public void removeSubscription(Long userId, Long subscriberId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + subscriberId));
        subscriptionRepository.deleteByUserAndSubscriber(user, subscriber);
    }

    @Override
    public List<EventShortDto> getEventsFromSubscriptions(Long userId, String text, List<Long> categories, Boolean paid,
                                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                          Boolean onlyAvailable, Integer from, Integer size,
                                                          EventSortOption sortOption) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        List<Long> subscriptions = subscriptionRepository.findSubscriptionsBySubscriber(user);
        if (subscriptions.isEmpty()) {
            return new LinkedList<>();
        }
        return eventService.getPublishedEventsOfUsers(subscriptions, text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, from, size, sortOption
        );
    }

    @Override
    public List<SubscriptionDto> getSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найден пользователь с id " + userId));
        return subscriptionRepository.findBySubscriber(user)
                .stream().map(mapper::toSubscriptionDto)
                .collect(Collectors.toList()
                );
    }
}
