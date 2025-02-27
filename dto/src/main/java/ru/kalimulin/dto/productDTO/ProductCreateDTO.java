package ru.kalimulin.dto.productDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreateDTO {

    @NotBlank(message = "Название товара не может быть пустым")
    private String title;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    private ProductStatus status = ProductStatus.AVAILABLE;

    @NotNull(message = "Количество товара не может быть пустым")
    @Min(value = 1, message = "Количество товара должно быть больше 0")
    private Integer stocks;

    @NotNull(message = "Категория не может быть пустой")
    private Long categoryId;

    @Size(max = 5, message = "Нельзя загрузить более 5 изображений")
    private List<ImageCreateDTO> images = new ArrayList<>();
}
