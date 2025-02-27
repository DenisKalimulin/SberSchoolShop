package ru.kalimulin.dto.reviewDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateDTO {
    @NotNull
    @Min(value = 1, message = "Оценка может быть от 1 до 5")
    @Max(value = 5, message = "Оценка может быть от 1 до 5")
    private int rating;

    private String comment;
}

