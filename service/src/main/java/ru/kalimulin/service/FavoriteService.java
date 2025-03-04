package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.customExceptions.favoriteExceptions.FavoriteNotFoundException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;

import java.util.List;

/**
 * Сервис для управления избранными товарами
 */
public interface FavoriteService {

    /**
     * Добавляет товар в избранное пользователя.
     *
     * @param productId ID товара, который нужно добавить.
     * @param session   Текущая сессия пользователя.
     * @throws UserNotFoundException    если пользователь не найден.
     * @throws ProductNotFoundException если товар не найден.
     * @throws IllegalStateException    если товар уже находится в избранном.
     */
    void addToFavorite(Long productId, HttpSession session);

    /**
     * Удаляет товар из избранного пользователя.
     *
     * @param productId ID товара, который нужно удалить.
     * @param session   Текущая сессия пользователя.
     * @throws UserNotFoundException     если пользователь не найден.
     * @throws ProductNotFoundException  если товар не найден.
     * @throws FavoriteNotFoundException если у пользователя нет избранного списка.
     */
    void removeFromFavorites(Long productId, HttpSession session);

    /**
     * Получает список товаров, добавленных в избранное.
     *
     * @param session Текущая сессия пользователя.
     * @return Список товаров в избранном.
     * @throws UserNotFoundException     если пользователь не найден.
     * @throws FavoriteNotFoundException если у пользователя нет избранного списка.
     */
    List<ProductResponseDTO> getFavorites(HttpSession session);
}
