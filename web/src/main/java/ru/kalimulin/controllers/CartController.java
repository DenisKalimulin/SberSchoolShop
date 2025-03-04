package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.cartDTO.CartDTO;
import ru.kalimulin.dto.cartItemDTO.CartItemCreateDTO;
import ru.kalimulin.service.CartService;

@RestController
@RequestMapping("/shop/cart")
@RequiredArgsConstructor
@Tag(name = "Корзина", description = "Методы для работы с корзиной покупок")
public class CartController {
    private final CartService cartService;
    private static Logger logger = LoggerFactory.getLogger(CartController.class);

    @Operation(summary = "Получить корзину пользователя", description = "Возвращает содержимое корзины текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Корзина успешно получена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @GetMapping
    public ResponseEntity<CartDTO> getCart(HttpSession session) {
        logger.info("Запрос на получение корзины");
        CartDTO cartDTO = cartService.getCart(session);
        return ResponseEntity.ok(cartDTO);
    }

    @Operation(summary = "Очистить корзину", description = "Удаляет все товары из корзины пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Корзина успешно очищена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        logger.info("Запрос на очищение корзины");
        cartService.clearCart(session);
        return ResponseEntity.ok("Корзина очищена!");
    }

    @Operation(summary = "Удалить товар из корзины", description = "Удаляет указанный товар из корзины пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно удален из корзины"),
            @ApiResponse(responseCode = "404", description = "Товар не найден в корзине"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeItemFromCart(
            @Parameter(description = "ID товара для удаления", example = "1")
            @PathVariable Long productId, HttpSession session) {
        logger.info("Запрос на удаление товара из корзины");
        cartService.removeItemFromCart(session, productId);
        return ResponseEntity.ok("Товар успешно удален из корзины!");
    }

    @Operation(summary = "Добавить товар в корзину", description = "Добавляет указанный товар в корзину пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно добавлен в корзину"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @PostMapping
    public ResponseEntity<String> addItemToCart(HttpSession session, @RequestBody CartItemCreateDTO cartItemCreateDTO) {
        logger.info("Запрос на добавление товара в корзину");
        cartService.addItemToCart(session, cartItemCreateDTO);
        return ResponseEntity.ok("Товар успешно добавлен в корзину!");
    }
}