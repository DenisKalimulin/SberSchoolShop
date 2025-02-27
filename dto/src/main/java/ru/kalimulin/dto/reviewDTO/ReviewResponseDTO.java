package ru.kalimulin.dto.reviewDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long id;
    private String sellerLogin;
    private String buyerLogin;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}