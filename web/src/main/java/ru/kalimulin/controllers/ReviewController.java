package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.reviewDTO.ReviewCreateDTO;
import ru.kalimulin.dto.reviewDTO.ReviewResponseDTO;
import ru.kalimulin.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/shop/review")
@RequiredArgsConstructor
@Tag(name = "Отзывы", description = "Методы для работы с отзывами")
public class ReviewController {
    private final ReviewService reviewService;
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Operation(summary = "Оставить отзыв", description = "Позволяет пользователю оставить отзыв после покупки у продавца")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв успешно оставлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "403", description = "Пользователь не имеет права оставить отзыв"),
            @ApiResponse(responseCode = "404", description = "Продавец не найден")
    })
    @PostMapping("/leave/{sellerId}")
    public ResponseEntity<ReviewResponseDTO> leaveReview(
            @Parameter(description = "ID продавца", example = "1")
            @PathVariable Long sellerId, @Valid @RequestBody ReviewCreateDTO reviewCreateDTO,
            HttpSession session) {
        logger.info("Запрос на добавление отзыва");
        ReviewResponseDTO reviewResponseDTO = reviewService.leaveReview(sellerId, reviewCreateDTO, session);
        return ResponseEntity.ok(reviewResponseDTO);
    }

    @Operation(summary = "Получить отзывы о продавце", description = "Возвращает список отзывов, оставленных о продавце")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список отзывов получен"),
            @ApiResponse(responseCode = "404", description = "Продавец не найден")
    })
    @GetMapping("/{sellerId}")
    public ResponseEntity<List<ReviewResponseDTO>> getSellerReviews(
            @Parameter(description = "ID продавца", example = "1")
            @PathVariable Long sellerId) {
        logger.info("Запрос на получение отзывов продавца");
        List<ReviewResponseDTO> review = reviewService.getSellerReviews(sellerId);
        return ResponseEntity.ok(review);
    }

    @Operation(summary = "Получить средний рейтинг продавца", description = "Возвращает средний рейтинг продавца на основе отзывов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Средний рейтинг получен"),
            @ApiResponse(responseCode = "404", description = "Продавец не найден")
    })
    @GetMapping("/average-rating/{sellerId}")
    public ResponseEntity<Double> getSellerAverageRating(
            @Parameter(description = "ID продавца", example = "1")
            @PathVariable Long sellerId) {
        logger.info("Запрос на получение среднего рейтинга продавца");
        Double rating = reviewService.getSellerAverageRating(sellerId);

        return ResponseEntity.ok(rating);
    }
}