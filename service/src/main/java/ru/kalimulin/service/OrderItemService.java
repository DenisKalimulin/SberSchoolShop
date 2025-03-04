package ru.kalimulin.service;

import ru.kalimulin.dto.orderItemDTO.OrderItemDTO;

/**
 * Сервис для управления товарами в заказе
 */
public interface OrderItemService {
    /**
     * Получает единицу товара из заказа
     *
     * @param id id товара
     * @return товар
     */
    OrderItemDTO getOrderItem(Long id);
}