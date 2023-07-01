package ru.practicum.explorewithme.model.event;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto toLocationDto(Location location);
}
