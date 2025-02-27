package ru.kalimulin.controllers;

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
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDTO> getOrderItem(@PathVariable Long id) {
        OrderItemDTO orderItemDTO = orderItemService.getOrderItem(id);
        return ResponseEntity.ok(orderItemDTO);
    }
}
