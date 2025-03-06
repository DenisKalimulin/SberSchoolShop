package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.cartExceptions.CartItemNotFoundException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.cartDTO.CartDTO;
import ru.kalimulin.dto.cartItemDTO.CartItemCreateDTO;
import ru.kalimulin.mappers.cartItemMapper.CartItemMapper;
import ru.kalimulin.mappers.cartMapper.CartMapper;
import ru.kalimulin.models.Cart;
import ru.kalimulin.models.CartItem;
import ru.kalimulin.models.Product;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.CartItemRepository;
import ru.kalimulin.repositories.CartRepository;
import ru.kalimulin.repositories.ProductRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.service.CartService;
import ru.kalimulin.util.SessionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Transactional
    @Override
    public CartDTO getCart(HttpSession session) {
        logger.info("Запрос на получение корзины");
        Cart cart = getOrCreateCart(SessionUtils.getUserLogin(session));
        return cartMapper.toCartDTO(cart);
    }

    @Transactional
    @Override
    public void addItemToCart(HttpSession session, CartItemCreateDTO cartItemCreateDTO) {
        logger.info("Запрос на добавление товара в корзину");
        Cart cart = getOrCreateCart(SessionUtils.getUserLogin(session));
        Product product = productRepository.findById(cartItemCreateDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(0)
                        .build());

        cartItem.setQuantity(cartItem.getQuantity() + cartItemCreateDTO.getQuantity());
        cartItemRepository.save(cartItem);

        updateCartTotalPrice(cart);
        logger.info("Товар добавлен в корзину");
    }

    @Transactional
    @Override
    public void removeItemFromCart(HttpSession session, Long productId) {
        logger.info("Запрос на удаление товара из корзины");

        Cart cart = getOrCreateCart(SessionUtils.getUserLogin(session));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new CartItemNotFoundException("Товар не найден в корзине"));

        cartItemRepository.delete(cartItem);

        cart.getItems().remove(cartItem);
        updateCartTotalPrice(cart);

        logger.info("Товар удален из корзины");
    }

    @Transactional
    @Override
    public void clearCart(HttpSession session) {
        logger.info("Запрос на очищение корзины");
        Cart cart = getOrCreateCart(SessionUtils.getUserLogin(session));
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        logger.info("Корзина очищена");
    }

    /**
     * Получает или создает корзину для пользователя.
     *
     * @param userLogin логин пользователя
     * @return объект корзины пользователя
     */
    private Cart getOrCreateCart(String userLogin) {
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .totalPrice(BigDecimal.ZERO)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Обновляет общую стоимость товаров в корзине.
     *
     * @param cart объект корзины пользователя
     */
    private void updateCartTotalPrice(Cart cart) {
        BigDecimal totalPrice = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }
}
