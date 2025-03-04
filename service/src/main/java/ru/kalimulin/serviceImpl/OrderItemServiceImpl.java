package ru.kalimulin.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kalimulin.customExceptions.orderExceptions.OrderItemNotFoundException;
import ru.kalimulin.dto.orderItemDTO.OrderItemDTO;
import ru.kalimulin.mappers.orderItemMapper.OrderItemMapper;
import ru.kalimulin.models.OrderItem;
import ru.kalimulin.repositories.OrderItemRepository;
import ru.kalimulin.service.OrderItemService;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public OrderItemDTO getOrderItem(Long id) {
        logger.info("Попытка получить товар из заказа с id {}", id);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException("Товар в заказе не найден"));

        return orderItemMapper.toOrderItemDTO(orderItem);
    }
}
