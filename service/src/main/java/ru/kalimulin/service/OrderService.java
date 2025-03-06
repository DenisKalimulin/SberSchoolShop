package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.orderDTO.OrderDTO;
import ru.kalimulin.customExceptions.userExceptions.*;
import ru.kalimulin.customExceptions.cartExceptions.*;
import ru.kalimulin.customExceptions.orderExceptions.*;
import ru.kalimulin.customExceptions.walletExceptions.*;
import ru.kalimulin.customExceptions.productExceptions.NotEnoughStockException;

import java.util.List;

/**
 * Сервис для управления заказами.
 */
public interface OrderService {

    /**
     * Создает заказ на основе товаров в корзине пользователя.
     *
     * @param session текущая сессия пользователя.
     * @return DTO с информацией о созданном заказе.
     * @throws UserNotFoundException если пользователь не найден.
     * @throws CartNotFoundException если корзина пользователя не найдена.
     * @throws CartIsEmptyException  если корзина пуста.
     */
    OrderDTO createOrder(HttpSession session);

    /**
     * Осуществляет оплату заказа.
     *
     * @param orderId идентификатор заказа.
     * @param session текущая сессия пользователя.
     * @return DTO с информацией об оплачиваемом заказе.
     * @throws UserNotFoundException      если пользователь не найден.
     * @throws OrderNotFoundException     если заказ не найден.
     * @throws PaymentProcessingException если произошла ошибка при обработке платежа.
     * @throws NotEnoughStockException    если товара недостаточно на складе.
     */
    OrderDTO paymentOrder(Long orderId, HttpSession session, Long addressId);

    /**
     * Получает список всех заказов пользователя.
     *
     * @param session текущая сессия пользователя.
     * @return список заказов пользователя.
     * @throws UserNotFoundException  если пользователь не найден.
     * @throws OrderNotFoundException если заказы не найдены.
     */
    List<OrderDTO> getUserOrders(HttpSession session);

    /**
     * Удаляет неоплаченный заказ.
     *
     * @param id      идентификатор заказа.
     * @param session текущая сессия пользователя.
     * @throws UserNotFoundException              если пользователь не найден.
     * @throws OrderNotFoundException             если заказ не найден.
     * @throws UnauthorizedOrderDeletionException если пользователь пытается удалить чужой заказ.
     * @throws OrderCannotBeDeletedException      если заказ уже оплачен и не может быть удален.
     */
    void deleteUnpaidOrder(Long id, HttpSession session);
}
