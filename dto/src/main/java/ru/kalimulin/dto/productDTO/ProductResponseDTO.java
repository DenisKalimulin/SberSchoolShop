package ru.kalimulin.dto.productDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stocks;
    private String sellerEmail;
    private String categoryName;
    private ProductStatus status;
    private List<ImageResponseDTO> images;
}