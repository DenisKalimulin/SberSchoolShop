package ru.kalimulin.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.cartExceptions.CartItemNotFoundException;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.mappers.cartItemMapper.CartItemMapper;
import ru.kalimulin.models.CartItem;
import ru.kalimulin.repositories.CartItemRepository;
import ru.kalimulin.service.CartItemService;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private static final Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);

    @Transactional
    @Override
    public CartItemDTO getCartItem(Long id) {
        logger.info("Запрос на получение единицы товара из корзины");
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("Товар в корзине не найден"));
        return cartItemMapper.toCartItemDTO(cartItem);
    }
}