package ru.kalimulin.mappers.cartItemMapper;

import org.springframework.stereotype.Component;
import ru.kalimulin.dto.cartItemDTO.CartItemCreateDTO;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;
import ru.kalimulin.models.Cart;
import ru.kalimulin.models.CartItem;
import ru.kalimulin.models.Product;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartItemMapperImpl implements CartItemMapper {
    @Override
    public CartItemDTO toCartItemDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        return CartItemDTO.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getTitle())
                .price(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .build();

    }

    @Override
    public CartItem toCartItem(CartItemCreateDTO cartItemCreateDTO, Product product, Cart cart) {
        if(cartItemCreateDTO == null || product == null || cart == null) {
            return null;
        }
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartItemCreateDTO.getQuantity())
                .build();
    }

    @Override
    public List<CartItemDTO> toCartItemDTOList(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return List.of();
        }

        return cartItems.stream()
                .map(this::toCartItemDTO)
                .collect(Collectors.toList());
    }
}
