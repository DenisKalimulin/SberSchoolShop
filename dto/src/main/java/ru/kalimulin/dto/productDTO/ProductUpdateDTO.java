package ru.kalimulin.dto.productDTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateDTO {

    private Long id;

    @Size(min = 3, max = 255, message = "Название должно быть от 3 до 255 символов")
    private String title;

    @Size(min = 10, message = "Описание должно содержать минимум 10 символов")
    private String description;

    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @Min(value = 0, message = "Количество товара не может быть отрицательным")
    private Integer stocks;

    private Long categoryId;

    @Size(max = 5, message = "Нельзя загрузить более 5 изображений")
    private List<String> imageUrls;
}