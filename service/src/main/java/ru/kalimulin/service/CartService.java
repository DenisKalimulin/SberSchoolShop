package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.cartDTO.CartDTO;
import ru.kalimulin.dto.cartItemDTO.CartItemCreateDTO;

public interface CartService {
    CartDTO getCart(HttpSession session);

    void addItemToCart(HttpSession session, CartItemCreateDTO cartItemCreateDTO);

    void removeItemFromCart(HttpSession session, Long productId);

    void clearCart(HttpSession session);
}
