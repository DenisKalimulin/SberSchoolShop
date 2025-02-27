package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.cartDTO.CartDTO;
import ru.kalimulin.dto.cartItemDTO.CartItemCreateDTO;
import ru.kalimulin.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Получить корзину пользователя
     */
    @GetMapping
    public ResponseEntity<CartDTO> getCart(HttpSession session) {
        CartDTO cartDTO = cartService.getCart(session);
        return ResponseEntity.ok(cartDTO);
    }

    /**
     * Очистить корзину пользователя
     */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ResponseEntity.ok("Корзина очищена!");
    }

    /**
     * Удалить товар из корзины
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeItemFromCart(@PathVariable Long productId, HttpSession session) {
        cartService.removeItemFromCart(session, productId);
        return ResponseEntity.ok("Товар успешно удален из корзины!");
    }

    /**
     * Добавить товар в корзину
     */
    @PostMapping
    public ResponseEntity<String> addItemToCart(HttpSession session, @RequestBody CartItemCreateDTO cartItemCreateDTO) {
        cartService.addItemToCart(session, cartItemCreateDTO);
        return ResponseEntity.ok("Товар успешно добавлен в корзину!");
    }

}