package ru.kalimulin.mappers.orderItemMapper;

import org.springframework.stereotype.Component;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.dto.orderItemDTO.OrderItemDTO;
import ru.kalimulin.models.CartItem;
import ru.kalimulin.models.Order;
import ru.kalimulin.models.OrderItem;

@Component
public class OrderItemMapperImpl implements OrderItemMapper {
    @Override
    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getTitle())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}