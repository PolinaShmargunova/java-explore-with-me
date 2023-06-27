package ru.practicum.explorewithme.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.model.CustomPageRequest;
import ru.practicum.explorewithme.model.compilation.CompilationDto;
import ru.practicum.explorewithme.service.compilation.CompilationService;
import ru.practicum.explorewithme.utils.CommonUtils;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCompilationController {
    final CompilationService compilationService;

    @Autowired
    public PublicCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_FROM) @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_SIZE) @Positive Integer size) {
        log.trace("Запрос подборок pinned = {}", pinned);
        return compilationService.getCompilations(pinned, new CustomPageRequest(from, size));
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.trace("Запрос подборки с id {}", compId);
        return compilationService.getCompilationById(compId);
    }
}
