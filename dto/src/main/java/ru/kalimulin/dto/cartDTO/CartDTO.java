package ru.kalimulin.dto.cartDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kalimulin.dto.cartItemDTO.CartItemDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long id;
    private String userLogin;
    private List<CartItemDTO> items;
    private BigDecimal totalPrice;
}