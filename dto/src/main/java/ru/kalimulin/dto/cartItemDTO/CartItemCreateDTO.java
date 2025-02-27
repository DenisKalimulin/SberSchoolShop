package ru.kalimulin.dto.cartItemDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemCreateDTO {
    private Long productId;
    private Integer quantity;
}