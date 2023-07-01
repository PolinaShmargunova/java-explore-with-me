package ru.practicum.explorewithme.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.model.category.CategoryDto;
import ru.practicum.explorewithme.model.category.NewCategoryDto;
import ru.practicum.explorewithme.service.category.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/admin/categories")
@Validated
public class AdminCategoryController {
    final CategoryService service;

    @Autowired
    public AdminCategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.trace("Создание категории: {}", categoryDto);
        return service.createCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.trace("Удаление категории с id {}", catId);
        service.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable Long catId, @RequestBody @Valid CategoryDto dto) {
        log.trace("Обновление категории с id {}", catId);
        return service.patchCategory(catId, dto);
    }
}
