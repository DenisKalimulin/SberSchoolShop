package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.cartExceptions.CartIsEmptyException;
import ru.kalimulin.customExceptions.cartExceptions.CartNotFoundException;
import ru.kalimulin.customExceptions.orderExceptions.OrderNotFoundException;
import ru.kalimulin.customExceptions.productExceptions.NotEnoughStockException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.customExceptions.walletExceptions.PaymentProcessingException;
import ru.kalimulin.customExceptions.walletExceptions.WalletNotFoundException;
import ru.kalimulin.dto.orderDTO.OrderDTO;
import ru.kalimulin.enums.OrderStatus;
import ru.kalimulin.mappers.orderMapper.OrderMapper;
import ru.kalimulin.models.*;
import ru.kalimulin.repositories.*;
import ru.kalimulin.service.CartService;
import ru.kalimulin.service.OrderService;
import ru.kalimulin.stubService.PaymentService;
import ru.kalimulin.util.SessionUtils;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final PaymentService paymentService;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
                            OrderMapper orderMapper, CartRepository cartRepository,
                            PaymentService paymentService, CartService cartService,
                            ProductRepository productRepository, WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.cartRepository = cartRepository;
        this.paymentService = paymentService;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    @Override
    public OrderDTO createOrder(HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Корзина пользователя " + userLogin + " не найдена"));

        Order order = createOrderFromCart(user, cart.getTotalPrice(), cart);

        order = orderRepository.save(order);

        return orderMapper.toOrderDTO(order);
    }

    @Transactional
    @Override
    public OrderDTO paymentOrder(Long orderId, HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Заказ с ID " + orderId + " не найден"));

        if (!order.getUser().equals(user)) {
            throw new PaymentProcessingException("Вы не можете оплатить чужой заказ!");
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

                product.setStocks(product.getStocks() - item.getQuantity());
                productRepository.save(product);

                User seller = product.getOwner();
                Wallet sellerWallet = walletRepository.findByUser(seller)
                        .orElseThrow(() -> new WalletNotFoundException("Кошелек продавца не найден"));

                sellerWallet.setBalance(sellerWallet.getBalance()
                        .add(BigDecimal.valueOf(item.getQuantity()).multiply(item.getPrice())));

                walletRepository.save(sellerWallet);
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
        String userLogin = SessionUtils.getUserLogin(session);
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));

        List<Order> userOrders = orderRepository.findByUser(user)
                .orElseThrow(() -> new OrderNotFoundException("История покупок не найдена"));

        return orderMapper.toListOrderDTO(userOrders);
    }

    /**
     * @param user
     * @param amount
     * @param cart
     * @return
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