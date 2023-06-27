package ru.practicum.explorewithme.model.subscription;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.model.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SubscriptionMapper {
    SubscriptionDto toSubscriptionDto(Subscription subscription);
}
