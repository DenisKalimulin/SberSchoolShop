package ru.kalimulin.mappers.orderItemMapper;

import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.dto.orderItemDTO.OrderItemDTO;
import ru.kalimulin.models.OrderItem;

public interface OrderItemMapper {
    OrderItemDTO toOrderItemDTO(OrderItem orderItem);
}
