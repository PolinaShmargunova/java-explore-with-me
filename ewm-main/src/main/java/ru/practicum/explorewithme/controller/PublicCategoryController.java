package ru.practicum.explorewithme.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.model.CustomPageRequest;
import ru.practicum.explorewithme.model.category.CategoryDto;
import ru.practicum.explorewithme.service.category.CategoryService;
import ru.practicum.explorewithme.utils.CommonUtils;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCategoryController {
    final CategoryService service;

    @Autowired
    public PublicCategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_FROM)
                                               @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = CommonUtils.PAGINATION_DEFAULT_SIZE)
                                           @Positive Integer size) {
        log.trace("Запрос категорий");
        return service.getCategories(new CustomPageRequest(from, size, Sort.unsorted()));
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.trace("Запрос категории {}", catId);
        return service.getCategoryById(catId);
    }
}
