package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/shop/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> addToFavorite(@PathVariable Long productId, HttpSession session) {
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

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromFavorites(@PathVariable Long productId, HttpSession session) {
        logger.info("Запрос на удаление товара из избранного");

        favoriteService.removeFromFavorites(productId, session);

        logger.info("Товар удален из избранного");
        return ResponseEntity.ok("Объявление удалено из избранного");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getFavorites(HttpSession session) {
        logger.info("Запрос на получение списка избранного");
        List<ProductResponseDTO> favorites = favoriteService.getFavorites(session);

        logger.info("Пользователь получил список избранного");
        return ResponseEntity.ok(favorites);
    }
}
