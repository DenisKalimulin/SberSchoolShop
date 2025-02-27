package ru.kalimulin.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.cartExceptions.CartItemNotFoundException;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.mappers.cartItemMapper.CartItemMapper;
import ru.kalimulin.models.CartItem;
import ru.kalimulin.repositories.CartItemRepository;
import ru.kalimulin.service.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartItemServiceImpl(CartItemRepository cartItemRepository,
                               CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
    }

    @Transactional
    @Override
    public CartItemDTO getCartItem(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new CartItemNotFoundException("Товар в корзине не найден"));
        return cartItemMapper.toCartItemDTO(cartItem);
    }
}