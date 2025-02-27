package ru.kalimulin.mappers.reviewMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kalimulin.dto.reviewDTO.ReviewCreateDTO;
import ru.kalimulin.dto.reviewDTO.ReviewResponseDTO;
import ru.kalimulin.models.Review;
import ru.kalimulin.models.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewMapperImplTest {
    private ReviewMapperImpl reviewMapper;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapperImpl();
    }

    @Test
    void toReviewResponseDTO() {
        User seller = User.builder().login("seller123").build();
        User buyer = User.builder().login("buyer456").build();

        Review review = Review.builder()
                .id(1L)
                .seller(seller)
                .buyer(buyer)
                .rating(5)
                .comment("Отличный продавец!")
                .createdAt(LocalDateTime.of(2025, 2, 24, 10, 0))
                .build();

        ReviewResponseDTO result = reviewMapper.toReviewResponseDTO(review);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSellerLogin()).isEqualTo("seller123");
        assertThat(result.getBuyerLogin()).isEqualTo("buyer456");
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Отличный продавец!");
        assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 2, 24, 10, 0));
    }


    @Test
    void toReview() {
        User seller = User.builder().login("seller123").build();
        User buyer = User.builder().login("buyer456").build();

        ReviewCreateDTO reviewCreateDTO = ReviewCreateDTO.builder()
                .rating(4)
                .comment("Хороший сервис")
                .build();

        Review result = reviewMapper.toReview(reviewCreateDTO, seller, buyer);

        assertThat(result).isNotNull();
        assertThat(result.getSeller()).isEqualTo(seller);
        assertThat(result.getBuyer()).isEqualTo(buyer);
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Хороший сервис");
        assertThat(result.getCreatedAt()).isNotNull();
    }
}
