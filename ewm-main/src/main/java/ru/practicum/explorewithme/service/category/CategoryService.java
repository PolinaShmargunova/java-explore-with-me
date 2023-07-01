package ru.practicum.explorewithme.service.category;

import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.model.category.CategoryDto;
import ru.practicum.explorewithme.model.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long id);

    CategoryDto patchCategory(Long id, CategoryDto dto);

    List<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategoryById(Long id);
}
