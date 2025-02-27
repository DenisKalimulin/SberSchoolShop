package ru.kalimulin.mappers.cartItemMapper;

import ru.kalimulin.dto.cartItemDTO.CartItemCreateDTO;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.models.Cart;
import ru.kalimulin.models.CartItem;
import ru.kalimulin.models.Product;

import java.util.List;

public interface CartItemMapper {
    CartItemDTO toCartItemDTO(CartItem cartItem);

    CartItem toCartItem(CartItemCreateDTO cartItemCreateDTO, Product product, Cart cart);

    List<CartItemDTO> toCartItemDTOList(List<CartItem> cartItems);
}