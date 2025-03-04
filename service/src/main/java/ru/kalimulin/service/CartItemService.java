package ru.kalimulin.service;

import ru.kalimulin.dto.cartItemDTO.CartItemDTO;

public interface CartItemService {
    /**
     * Получает товар из корзины по id.
     *
     * @param id - id товара
     * @return товар из корзины
     */
    CartItemDTO getCartItem(Long id);
}