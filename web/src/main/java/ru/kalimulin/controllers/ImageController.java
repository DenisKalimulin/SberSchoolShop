package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.service.ImageService;

import java.util.List;

@RestController
@RequestMapping("/shop/images")
@RequiredArgsConstructor
@Tag(name = "Изображения", description = "Методы для управления изображениями товаров")
public class ImageController {
    private final ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Operation(summary = "Добавить изображение к товару", description = "Добавляет изображение к указанному товару")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Изображение успешно добавлено"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации изображения")
    })
    @PostMapping("/add/{productId}")
    public ResponseEntity<ImageResponseDTO> addImageToProduct(
            @Parameter(description = "ID товара, к которому добавляется изображение", example = "1")
            @PathVariable Long productId,
            @RequestBody ImageCreateDTO imageCreateDTO) {
        logger.info("Добавление изображения к продукту с id {}", productId);
        ImageResponseDTO imageResponseDTO = imageService.addImageToProduct(productId, imageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageResponseDTO);
    }

    @Operation(summary = "Получить изображения товара", description = "Возвращает список изображений для указанного товара")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список изображений успешно получен"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "204", description = "Нет изображений у данного товара")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ImageResponseDTO>> getImagesByProduct(
            @Parameter(description = "ID товара", example = "1")
            @PathVariable Long productId) {
        logger.info("Запрос изображений для товара с id {}", productId);
        List<ImageResponseDTO> images = imageService.getImagesByProductId(productId);

        if (images.isEmpty()) {
            logger.info("Для товара {} нет изображений", productId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(images);
    }

    @Operation(summary = "Удалить изображение", description = "Удаляет изображение по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображение успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено")
    })
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<String> deleteImage(
            @Parameter(description = "ID изображения", example = "10")
            @PathVariable Long imageId) {
        logger.info("Удаление изображения с id {}", imageId);
        imageService.deleteImageById(imageId);
        return ResponseEntity.ok("Изображение успешно удалено");
    }
}