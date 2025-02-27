package ru.kalimulin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.service.CartItemService;

@RestController
@RequestMapping("/cart/items")
public class CartItemController {
    private final CartItemService cartItemService;

    @Autowired
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItemDTO> getCartItem(@PathVariable Long id) {
        CartItemDTO cartItemDTO = cartItemService.getCartItem(id);
        return ResponseEntity.ok(cartItemDTO);
    }
}