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
import ru.kalimulin.dto.orderDTO.OrderDTO;
import ru.kalimulin.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/shop/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "Методы для управления заказами(Создание, оплата)")
public class OrderController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Operation(summary = "Создать заказ", description = "Создает заказ на основе товаров в корзине текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден или корзина пуста")
    })
    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(HttpSession session) {
        logger.info("Запрос на создание заказа");
        OrderDTO orderDTO = orderService.createOrder(session);
        return ResponseEntity.ok(orderDTO);
    }

    @Operation(summary = "Оплатить заказ", description = "Оплачивает заказ и изменяет его статус на 'Оплачено'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно оплачен"),
            @ApiResponse(responseCode = "400", description = "Ошибка обработки платежа"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<OrderDTO> paymentOrder(
            @Parameter(description = "ID заказа, который требуется оплатить", example = "1")
            @PathVariable Long orderId, HttpSession session) {
        logger.info("Запрос на оплату заказа");
        OrderDTO orderDTO = orderService.paymentOrder(orderId, session);
        return ResponseEntity.ok(orderDTO);
    }

    @Operation(summary = "Получить заказы пользователя", description = "Возвращает список всех заказов текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заказов успешно получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "204", description = "У пользователя нет заказов")
    })
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders(HttpSession session) {
        logger.info("Запрос на получение заказов");
        List<OrderDTO> orders = orderService.getUserOrders(session);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Удалить заказ пользователя", description = "Удаляет не оплаченный заказ по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав на удаление заказа"),
            @ApiResponse(responseCode = "400", description = "Попытка удалить оплаченный заказ")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @Parameter(description = "ID заказа, который требуется удалить", example = "1")
            @PathVariable Long id, HttpSession session) {
        logger.info("Запрос на удаление заказа");

        orderService.deleteUnpaidOrder(id, session);
        return ResponseEntity.ok("Заказ успешно удален");
    }
}