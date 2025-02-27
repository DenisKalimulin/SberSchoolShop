package ru.kalimulin.dto.imageDTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageCreateDTO {
    @NotNull(message = "Нужно указать id товара")
    private Long id;
    @NotNull(message = "URL изображения не может быть пустым")
    private String imageUrl;
}

