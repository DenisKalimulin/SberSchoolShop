package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kalimulin.dto.orderItemDTO.OrderItemDTO;
import ru.kalimulin.service.OrderItemService;

@RestController
@RequestMapping("/order/items")
@RequiredArgsConstructor
@Tag(name = "Элемент заказа", description = "Методы для управления элементами заказа")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Operation(summary = "Получить элемент заказа",
            description = "Возвращает информацию о конкретном элементе заказа по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Элемент заказа успешно получен"),
            @ApiResponse(responseCode = "404", description = "Элемент заказа не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> getOrderItem(
            @Parameter(description = "ID элемента заказа", example = "1")
            @PathVariable Long id) {
        OrderItemDTO orderItemDTO = orderItemService.getOrderItem(id);
        return ResponseEntity.ok(orderItemDTO);
    }
}