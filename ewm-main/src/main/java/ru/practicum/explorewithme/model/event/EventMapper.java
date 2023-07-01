package ru.practicum.explorewithme.model.event;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.model.category.CategoryMapper;
import ru.practicum.explorewithme.model.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class})
public interface EventMapper {

    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
