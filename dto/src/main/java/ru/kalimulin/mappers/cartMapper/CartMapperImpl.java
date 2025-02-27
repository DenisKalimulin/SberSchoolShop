package ru.kalimulin.mappers.cartMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kalimulin.dto.cartDTO.CartDTO;
import ru.kalimulin.mappers.cartItemMapper.CartItemMapper;
import ru.kalimulin.models.Cart;

import java.util.stream.Collectors;

@Component
public class CartMapperImpl implements CartMapper {
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartMapperImpl(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    public CartDTO toCartDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        return CartDTO.builder()
                .id(cart.getId())
                .userLogin(cart.getUser().getLogin())
                .items(cart.getItems().stream()
                        .map(cartItemMapper::toCartItemDTO)
                        .collect(Collectors.toList()))
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}