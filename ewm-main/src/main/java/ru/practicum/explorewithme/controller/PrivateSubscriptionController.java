package ru.practicum.explorewithme.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.model.EventSortOption;
import ru.practicum.explorewithme.model.event.EventShortDto;
import ru.practicum.explorewithme.model.exception.ConvertationException;
import ru.practicum.explorewithme.model.subscription.SubscriptionDto;
import ru.practicum.explorewithme.service.subscription.SubscriptionService;
import ru.practicum.explorewithme.utils.CommonUtils;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/subscriptions")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateSubscriptionController {

    final SubscriptionService subscriptionService;

    @Autowired
    public PrivateSubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{subscribeTarget}")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto createSubscription(@PathVariable Long userId, @PathVariable Long subscribeTarget) {
        log.trace("Пользователь {} подписывается на {}", userId, subscribeTarget);
        return subscriptionService.createSubscription(subscribeTarget, userId);
    }

    @DeleteMapping("/{subscribeTarget}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSubscription(@PathVariable Long userId, @PathVariable Long subscribeTarget) {
        log.trace("Пользователь {} подписывается на {}", userId, subscribeTarget);
        subscriptionService.removeSubscription(subscribeTarget, userId);
    }

    @GetMapping()
    public List<SubscriptionDto> getSubscriptions(@PathVariable Long userId) {
        log.trace("Запрос подписок пользователя {}", userId);
        return subscriptionService.getSubscriptions(userId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsFromSubscription(@PathVariable Long userId,
                                                         @RequestParam(required = false) String text,
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
                                                         @RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_SIZE) @Positive Integer size) {
        log.trace("Поиск событий из подписок пользователя {} : текст = '{}', категории {}, платные {} за период {}-{} только доступные {}",
                userId, text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        EventSortOption sortOption = null;
        if (sort != null) {
            sortOption = EventSortOption.from(sort).orElseThrow(() -> {
                throw new ConvertationException("Не удалось найти EventSortOption " + sort);
            });
        }
        return subscriptionService.getEventsFromSubscriptions(userId, text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, from, size, sortOption);
    }
}
