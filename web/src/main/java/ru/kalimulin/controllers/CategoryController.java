package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.categoryDTO.CategoryResponseDTO;
import ru.kalimulin.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/shop/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @Parameter(description = "ID категории", example = "1") @PathVariable Long id) {
        logger.info("Запрос категории по ID {}", id);
        CategoryResponseDTO category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/name")
    public ResponseEntity<CategoryResponseDTO> getCategoryByName(
            @Parameter(description = "Название категории", example = "Электроника") @RequestParam String name) {
        logger.info("Запрос по названию {}", name);
        CategoryResponseDTO category = categoryService.findByName(name);
        return ResponseEntity.ok(category);
    }
}