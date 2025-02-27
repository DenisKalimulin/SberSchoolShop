package ru.kalimulin.service;

import ru.kalimulin.dto.orderItemDTO.OrderItemDTO;

public interface OrderItemService {
    OrderItemDTO getOrderItem(Long id);
}
