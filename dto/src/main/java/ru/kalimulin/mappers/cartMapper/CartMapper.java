package ru.kalimulin.mappers.cartMapper;

import ru.kalimulin.dto.cartDTO.CartDTO;
import ru.kalimulin.models.Cart;

public interface CartMapper {
    CartDTO toCartDTO(Cart cart);
}