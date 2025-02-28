package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.service.CartItemService;

@RestController
@RequestMapping("/cart/items")
@RequiredArgsConstructor
@Tag(name = "Элемент корзины", description = "Методы для работы с элементами корзины")
public class CartItemController {
    private final CartItemService cartItemService;

    @Operation(summary = "Получить элемент корзины", description = "Возвращает информацию о конкретном элементе корзины по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Элемент корзины успешно получен"),
            @ApiResponse(responseCode = "404", description = "Элемент корзины не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CartItemDTO> getCartItem(
            @Parameter(description = "ID элемента корзины", example = "1")
            @PathVariable Long id) {
        CartItemDTO cartItemDTO = cartItemService.getCartItem(id);
        return ResponseEntity.ok(cartItemDTO);
    }
}