package ru.kalimulin.service;

import ru.kalimulin.dto.categoryDTO.CategoryCreateDTO;
import ru.kalimulin.dto.categoryDTO.CategoryResponseDTO;
import ru.kalimulin.dto.categoryDTO.CategoryUpdateDTO;

import java.util.List;

/**
 * Сервис для управления категориями товаров.
 */
public interface CategoryService {

    /**
     * Поиск категории по её ID.
     *
     * @param id ID категории.
     * @return DTO с данными найденной категории.
     */
    CategoryResponseDTO findById(Long id);

    /**
     * Получение списка всех категорий.
     *
     * @return Список DTO с данными всех категорий.
     */
    List<CategoryResponseDTO> findAll();

    /**
     * Поиск категории по названию.
     *
     * @param name Название категории.
     * @return DTO с данными найденной категории.
     */
    CategoryResponseDTO findByName(String name);

    /**
     * Создание новой категории.
     *
     * @param categoryCreateDTO DTO с данными для создания категории.
     * @return DTO с созданной категорией.
     */
    CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO);

    /**
     * Обновление данных категории.
     *
     * @param categoryUpdateDTO DTO с обновлёнными данными категории.
     * @return DTO с обновлённой категорией.
     */
    CategoryResponseDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO);

    /**
     * Удаление категории по её ID.
     *
     * @param id ID категории.
     */
    void deleteCategory(Long id);
}
