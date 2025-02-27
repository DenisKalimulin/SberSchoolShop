package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.orderDTO.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(HttpSession session);

    OrderDTO paymentOrder(Long orderId, HttpSession session);

    List<OrderDTO> getUserOrders(HttpSession session);
}