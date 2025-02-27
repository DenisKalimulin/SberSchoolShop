package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.orderDTO.OrderDTO;
import ru.kalimulin.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/shop/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(HttpSession session) {
        OrderDTO orderDTO = orderService.createOrder(session);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/payment/{orderId}")
    public ResponseEntity<OrderDTO> paymentOrder(@PathVariable Long orderId, HttpSession session) {
        OrderDTO orderDTO = orderService.paymentOrder(orderId, session);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders(HttpSession session) {
        List<OrderDTO> orders = orderService.getUserOrders(session);
        return ResponseEntity.ok(orders);
    }
}