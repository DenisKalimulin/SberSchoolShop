package ru.kalimulin.mappers.orderMapper;

import ru.kalimulin.dto.orderDTO.OrderDTO;
import ru.kalimulin.models.Order;

import java.util.List;

public interface OrderMapper {
    OrderDTO toOrderDTO(Order order);
    List<OrderDTO> toListOrderDTO(List<Order> orders);
}