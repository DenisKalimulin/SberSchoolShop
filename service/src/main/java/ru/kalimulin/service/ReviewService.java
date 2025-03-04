package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.customExceptions.reviewExceptions.ReviewException;
import ru.kalimulin.customExceptions.reviewExceptions.ReviewNotFoundException;
import ru.kalimulin.dto.reviewDTO.ReviewCreateDTO;
import ru.kalimulin.dto.reviewDTO.ReviewResponseDTO;

import java.util.List;

/**
 * Сервис для управления отзывами пользователей.
 */
public interface ReviewService {

    /**
     * Оставляет отзыв продавцу после покупки.
     *
     * @param sellerId        ID продавца, которому оставляют отзыв.
     * @param reviewCreateDTO DTO с данными отзыва.
     * @param session         текущая сессия пользователя.
     * @return созданный отзыв в виде DTO.
     * @throws ReviewException если пользователь не покупал у продавца.
     */
    ReviewResponseDTO leaveReview(Long sellerId, ReviewCreateDTO reviewCreateDTO, HttpSession session);

    /**
     * Получает список отзывов для продавца.
     *
     * @param sellerId ID продавца.
     * @return список DTO с отзывами.
     */
    List<ReviewResponseDTO> getSellerReviews(Long sellerId);

    /**
     * Получает средний рейтинг продавца.
     *
     * @param sellerId ID продавца.
     * @return средний рейтинг.
     */
    double getSellerAverageRating(Long sellerId);

    /**
     * Удаляет отзыв по ID.
     *
     * @param id ID отзыва.
     * @throws ReviewNotFoundException если отзыв не найден.
     */
    void deleteReview(Long id);
}