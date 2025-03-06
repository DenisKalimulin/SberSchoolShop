package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.addressExceptions.AddressNotFoundException;
import ru.kalimulin.customExceptions.addressExceptions.AddressNotProvidedException;
import ru.kalimulin.customExceptions.addressExceptions.UserHasNoAddressException;
import ru.kalimulin.customExceptions.cartExceptions.CartIsEmptyException;
import ru.kalimulin.customExceptions.cartExceptions.CartNotFoundException;
import ru.kalimulin.customExceptions.orderExceptions.OrderCannotBeDeletedException;
import ru.kalimulin.customExceptions.orderExceptions.OrderNotFoundException;
import ru.kalimulin.customExceptions.orderExceptions.UnauthorizedOrderDeletionException;
import ru.kalimulin.customExceptions.productExceptions.NotEnoughStockException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UnauthorizedAccessException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.customExceptions.walletExceptions.PaymentProcessingException;
import ru.kalimulin.customExceptions.walletExceptions.WalletNotFoundException;
import ru.kalimulin.dto.kafkaEventDTO.EmailNotificationEvent;
import ru.kalimulin.dto.orderDTO.OrderDTO;
import ru.kalimulin.enums.OrderStatus;
import ru.kalimulin.kafka.InventoryEventProducer;
import ru.kalimulin.kafka.KafkaOrderEventPublisher;
import ru.kalimulin.mappers.orderMapper.OrderMapper;
import ru.kalimulin.models.*;
import ru.kalimulin.repositories.*;
import ru.kalimulin.service.CartService;
import ru.kalimulin.service.OrderService;
import ru.kalimulin.stubService.PaymentService;
import ru.kalimulin.util.SessionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final PaymentService paymentService;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final WalletRepository walletRepository;
    private final AddressRepository addressRepository;
    private final InventoryEventProducer inventoryEventProducer;
    private final KafkaOrderEventPublisher kafkaOrderEventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Transactional
    @Override
    public OrderDTO createOrder(HttpSession session) {
        logger.info("Попытка создать заказ");
        String userLogin = SessionUtils.getUserLogin(session);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Корзина пользователя " + userLogin + " не найдена"));

        Order order = createOrderFromCart(user, cart.getTotalPrice(), cart);

        order = orderRepository.save(order);

        logger.info("Заказ успешно создан");

        return orderMapper.toOrderDTO(order);
    }

    @Transactional
    @Override
    public OrderDTO paymentOrder(Long orderId, HttpSession session, Long addressId) {
        logger.info("Попытка оплатить заказ");
        String userLogin = SessionUtils.getUserLogin(session);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + orderId + " не найден"));

        if (!order.getUser().equals(user)) {
            throw new PaymentProcessingException("Вы не можете оплатить чужой заказ!");
        }

        // Проверяем, есть ли у пользователя хотя бы один адрес
        List<Address> userAddresses = addressRepository.findAddressByUser(user);
        if (userAddresses.isEmpty()) {
            throw new UserHasNoAddressException("У вас нет сохраненного адреса. Добавьте адрес перед оплатой.");
        }

        // Если у пользователя есть адреса, но он не указал конкретный адрес
        if (addressId == null) {
            throw new AddressNotProvidedException("Вы не указали адрес для доставки.");
        }

        // Проверяем, принадлежит ли указанный адрес пользователю
        Address deliveryAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Адрес с ID " + addressId + " не найден."));
        if (!deliveryAddress.getUser().equals(user)) {
            throw new UnauthorizedAccessException("Вы не можете использовать этот адрес.");
        }

        boolean paymentSuccess = paymentService.processPayment(user, order.getTotalPrice());

        if (paymentSuccess) {
            order.setStatus(OrderStatus.PAID);

            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

                if (product.getStocks() < item.getQuantity()) {
                    throw new NotEnoughStockException("Недостаточно товара на складе: " + product.getTitle());
                }

                User seller = product.getOwner();
                Wallet sellerWallet = walletRepository.findByUser(seller)
                        .orElseThrow(() -> new WalletNotFoundException("Кошелек продавца не найден"));

                sellerWallet.setBalance(sellerWallet.getBalance()
                        .add(BigDecimal.valueOf(item.getQuantity()).multiply(item.getPrice())));

                walletRepository.save(sellerWallet);

                // Отправка Kafka-события для обновления остатков товара
                inventoryEventProducer.sendInventoryUpdate(product.getId(), item.getQuantity());

                // Отправляем Kafka-событие для продавца
                EmailNotificationEvent emailEvent = new EmailNotificationEvent(
                        seller.getEmail(),
                        product.getTitle(),
                        item.getQuantity(),
                        deliveryAddress.toString()
                );
                kafkaOrderEventPublisher.sendEmailNotification(emailEvent);
            }

            orderRepository.save(order);

            cartService.clearCart(session);
            return orderMapper.toOrderDTO(order);
        } else {
            throw new PaymentProcessingException("Ошибка при обработке платежа");
        }
    }

    @Transactional
    @Override
    public List<OrderDTO> getUserOrders(HttpSession session) {
        logger.info("Попытка получить список заказов пользователя");
        String userLogin = SessionUtils.getUserLogin(session);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));

        List<Order> userOrders = orderRepository.findByUser(user)
                .orElseThrow(() -> new OrderNotFoundException("История покупок не найдена"));

        logger.info("Заказы успешно получены");
        return orderMapper.toListOrderDTO(userOrders);
    }

    @Transactional
    @Override
    public void deleteUnpaidOrder(Long id, HttpSession session) {
        logger.info("Попытка удалить заказ по id {}", id);
        String userLogin = SessionUtils.getUserLogin(session);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с таким id " + id + " не найден"));

        if (!user.equals(order.getUser())) {
            throw new UnauthorizedOrderDeletionException("Вы не можете удалить не свой заказ");
        }

        if (order.getStatus() == OrderStatus.PENDING) {
            logger.info("Заказ успешно удален");
            orderRepository.delete(order);
        } else {
            logger.warn("Пользователь пытается удалить оплаченный заказ");
            throw new OrderCannotBeDeletedException("Можно удалить только неоплаченный заказ");
        }
    }

    /**
     * Создает новый заказ на основе содержимого корзины пользователя.
     *
     * @param user   пользователь, который оформляет заказ.
     * @param amount общая стоимость заказа.
     * @param cart   корзина пользователя, содержащая товары.
     * @return созданный объект заказа (Order).
     * @throws CartIsEmptyException если корзина пользователя пуста.
     */
    private Order createOrderFromCart(User user, BigDecimal amount, Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CartIsEmptyException("Ваша корзина пуста. Невозможно сделать заказ!");
        }

        List<CartItem> items = cart.getItems();

        // Переводим CartItems в список OrderItems
        List<OrderItem> orderItems = items.stream()
                .map(cartItem -> OrderItem.builder()
                        .product(cartItem.getProduct()) // Продукт из корзины
                        .quantity(cartItem.getQuantity()) // Количество из корзины
                        .price(cartItem.getProduct().getPrice()) // Цена товара из корзины
                        .build())
                .collect(Collectors.toList());


        // Создаем новый заказ
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(amount)
                .createdAt(LocalDateTime.now())
                .items(orderItems)
                .build();

        // Устанавливаем связь Order -> OrderItem
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        return order;
    }
}