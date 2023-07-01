package ru.practicum.explorewithme.model.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "events", expression = "java(events)")
    Compilation newDtoToCompilation(NewCompilationDto newCompilationDto, List<Event> events);

    @Mapping(target = "events", expression = "java(eventsShortDto)")
    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventsShortDto);
}
