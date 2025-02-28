package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.categoryDTO.CategoryResponseDTO;
import ru.kalimulin.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/shop/categories")
@RequiredArgsConstructor
@Tag(name = "Категории", description = "Методы для работы с категориями товаров")
public class CategoryController {
    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Operation(summary = "Получить все категории", description = "Возвращает список всех доступных категорий товаров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категории успешно получены"),
            @ApiResponse(responseCode = "204", description = "Категории не найдены")
    })
    @GetMapping()
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        logger.info("Запрос всех категорий");
        List<CategoryResponseDTO> categories = categoryService.findAll();

        if (categories.isEmpty()) {
            logger.info("В базе данных нет категорий");
        } else {
            logger.info("Запрос всех категорий: {} категорий найдено", categories.size());
        }

        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Получить категорию по ID", description = "Возвращает категорию товаров по её уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно найдена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @Parameter(description = "ID категории", example = "1") @PathVariable Long id) {
        logger.info("Запрос категории по ID {}", id);
        CategoryResponseDTO category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Получить категорию по названию", description = "Возвращает категорию товаров по её названию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно найдена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @GetMapping("/name")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(
            @Parameter(description = "Название категории", example = "Электроника")
            @RequestParam String name) {
        logger.info("Запрос по названию {}", name);
        CategoryResponseDTO category = categoryService.findByName(name);
        return ResponseEntity.ok(category);
    }
}