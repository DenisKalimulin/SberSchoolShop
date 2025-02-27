package ru.kalimulin.mappers.categoryMapper;

import ru.kalimulin.dto.categoryDTO.CategoryCreateDTO;
import ru.kalimulin.dto.categoryDTO.CategoryResponseDTO;
import ru.kalimulin.models.Category;

import java.util.List;

public interface CategoryMapper {
    CategoryResponseDTO toCategoryResponseDTO(Category category);

    Category toCategory(CategoryCreateDTO categoryCreateDTO);

    List<CategoryResponseDTO> toListCategoryResponseDTO(List<Category> categories);
}
