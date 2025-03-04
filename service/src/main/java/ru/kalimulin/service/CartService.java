package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.cartDTO.CartDTO;
import ru.kalimulin.dto.cartItemDTO.CartItemCreateDTO;

public interface CartService {
    /**
     * Получает корзину текущего пользователя.
     *
     * @param session текущая сессия пользователя
     * @return DTO объекта корзины
     */
    CartDTO getCart(HttpSession session);

    /**
     * Добавляет товар в корзину пользователя.
     *
     * @param session           текущая сессия пользователя
     * @param cartItemCreateDTO информация о добавляемом товаре
     */
    void addItemToCart(HttpSession session, CartItemCreateDTO cartItemCreateDTO);

    /**
     * Удаляет товар из корзины пользователя.
     *
     * @param session   текущая сессия пользователя
     * @param productId идентификатор удаляемого товара
     */
    void removeItemFromCart(HttpSession session, Long productId);

    /**
     * Очищает корзину пользователя.
     *
     * @param session текущая сессия пользователя
     */
    void clearCart(HttpSession session);
}