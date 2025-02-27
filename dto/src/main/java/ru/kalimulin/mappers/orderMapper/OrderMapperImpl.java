package ru.kalimulin.mappers.orderMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kalimulin.dto.orderDTO.OrderDTO;
import ru.kalimulin.mappers.orderItemMapper.OrderItemMapper;
import ru.kalimulin.models.Order;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapperImpl implements OrderMapper {
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderMapperImpl(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public OrderDTO toOrderDTO(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDTO.builder()
                .id(order.getId())
                .userLogin(order.getUser().getLogin())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(orderItemMapper::toOrderItemDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<OrderDTO> toListOrderDTO(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }
        return orders.stream()
                .map(this::toOrderDTO)
                .collect(Collectors.toList());

    }
}