package ru.kalimulin.service;

import ru.kalimulin.dto.cartItemDTO.CartItemDTO;

public interface CartItemService {
    CartItemDTO getCartItem(Long id);
}