package ru.kalimulin.service;

import ru.kalimulin.dto.categoryDTO.*;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO findById(Long id);

    List<CategoryResponseDTO> findAll();

    CategoryResponseDTO findByName(String name);

    CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO);

    CategoryResponseDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO);

    void deleteCategory(Long id);
}
