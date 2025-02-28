package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/shop/favorites")
@RequiredArgsConstructor
@Tag(name = "Избранное", description = "Методы для управления избранными товарами")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    @Operation(summary = "Добавить товар в избранное", description = "Добавляет товар в список избранного для текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Товар успешно добавлен в избранное"),
            @ApiResponse(responseCode = "400", description = "Товар уже находится в избранном"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @PostMapping("/{productId}")
    public ResponseEntity<String> addToFavorite(
            @Parameter(description = "ID товара", example = "1") @PathVariable Long productId,
            HttpSession session) {
        logger.info("Запрос на добавление товара в избранное");

        try {
            favoriteService.addToFavorite(productId, session);
            logger.info("Товар добавлен в избранное");
            return ResponseEntity.status(HttpStatus.CREATED).body("Объявление добавлено в избранное");
        } catch (IllegalStateException ex) {
            logger.warn("Товар уже в избранном");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Объявление уже в избранном");
        }
    }

    @Operation(summary = "Удалить товар из избранного", description = "Удаляет товар из списка избранного текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно удален из избранного"),
            @ApiResponse(responseCode = "404", description = "Товар не найден в избранном")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromFavorites(
            @Parameter(description = "ID товара", example = "1") @PathVariable Long productId,
            HttpSession session) {
        logger.info("Запрос на удаление товара из избранного");

        favoriteService.removeFromFavorites(productId, session);

        logger.info("Товар удален из избранного");
        return ResponseEntity.ok("Объявление удалено из избранного");
    }

    @Operation(summary = "Получить список избранного", description = "Возвращает список всех товаров, добавленных в избранное текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список избранного успешно получен"),
            @ApiResponse(responseCode = "204", description = "Список избранного пуст")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getFavorites(HttpSession session) {
        logger.info("Запрос на получение списка избранного");
        List<ProductResponseDTO> favorites = favoriteService.getFavorites(session);

        if (favorites.isEmpty()) {
            logger.info("Список избранного пуст");
            return ResponseEntity.noContent().build();
        }

        logger.info("Пользователь получил список избранного");
        return ResponseEntity.ok(favorites);
    }
}