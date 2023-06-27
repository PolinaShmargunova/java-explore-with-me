package ru.practicum.explorewithme.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.model.compilation.*;
import ru.practicum.explorewithme.model.event.Event;
import ru.practicum.explorewithme.model.event.EventMapper;
import ru.practicum.explorewithme.model.event.EventShortDto;
import ru.practicum.explorewithme.model.exception.BadRequestException;
import ru.practicum.explorewithme.model.exception.ObjectNotFoundException;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper mapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }
        if (compilationDto.getTitle().isEmpty() || compilationDto.getTitle() == null || compilationDto.getTitle().isBlank()) {
            throw new BadRequestException("Поле заголовок не заполнено");
        }
        if (50 < compilationDto.getTitle().length()) {
            throw new BadRequestException("Поле заголовок слишком длинное");
        }
        List<Event> events;
        if (compilationDto.getEvents() == null) {
            events = eventRepository.findAll();
        } else {
            events = eventRepository.findAllById(compilationDto.getEvents());
        }
        List<EventShortDto> shortDtos = events.stream()
                .map(eventMapper::toEventShortDto).collect(Collectors.toList());
        return mapper.toCompilationDto(compilationRepository.save(mapper.newDtoToCompilation(compilationDto, events)), shortDtos);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest request) {
        if (request.getTitle() != null && 50 < request.getTitle().length()) {
            throw new BadRequestException("Поле заголовок слишком длинное");
        }
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найдена подборка с id " + compId));
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            compilation.setEvents(eventRepository.findAllById(request.getEvents()));
        }
        List<EventShortDto> shortDtos = compilation.getEvents().stream()
                .map(eventMapper::toEventShortDto).collect(Collectors.toList());
        return mapper.toCompilationDto(compilationRepository.save(compilation), shortDtos);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        return compilationRepository.findByPinned(pinned, pageable).stream()
                .map(comp -> mapper.toCompilationDto(comp, comp.getEvents()
                        .stream().map(eventMapper::toEventShortDto).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Не найдена подборка с id " + compId));
        return mapper.toCompilationDto(compilation, compilation.getEvents()
                .stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()));
    }
}
