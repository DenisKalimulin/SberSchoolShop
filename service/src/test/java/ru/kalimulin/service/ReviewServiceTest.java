package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kalimulin.customExceptions.reviewExceptions.ReviewException;
import ru.kalimulin.customExceptions.reviewExceptions.ReviewNotFoundException;
import ru.kalimulin.dto.reviewDTO.ReviewCreateDTO;
import ru.kalimulin.dto.reviewDTO.ReviewResponseDTO;
import ru.kalimulin.mappers.reviewMapper.ReviewMapper;
import ru.kalimulin.models.Review;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.OrderRepository;
import ru.kalimulin.repositories.ReviewRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.serviceImpl.ReviewServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private HttpSession session;

    private User buyer;
    private User seller;
    private Review review;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setId(1L);
        buyer.setLogin("buyerUser");

        seller = new User();
        seller.setId(2L);
        seller.setLogin("sellerUser");

        review = Review.builder()
                .id(1L)
                .buyer(buyer)
                .seller(seller)
                .rating(5)
                .comment("Great seller!")
                .createdAt(LocalDateTime.now())
                .build();

        lenient().when(session.getAttribute("userLogin")).thenReturn("buyerUser");
        lenient().when(session.getAttribute("check")).thenReturn(true);
    }

    @Test
    void shouldLeaveReviewSuccessfully() {
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(5, "Great seller!");
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(1L, seller.getLogin(), buyer.getLogin(), 5, "Great seller!", LocalDateTime.now());

        when(userRepository.findByLogin("buyerUser")).thenReturn(Optional.of(buyer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(orderRepository.existsByUserAndSeller(buyer, seller)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toReviewResponseDTO(any(Review.class))).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.leaveReview(2L, reviewCreateDTO, session);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Great seller!", result.getComment());
    }

    @Test
    void shouldThrowExceptionWhenLeavingReviewWithoutPurchase() {
        ReviewCreateDTO reviewCreateDTO = new ReviewCreateDTO(5, "Great seller!");

        when(userRepository.findByLogin("buyerUser")).thenReturn(Optional.of(buyer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(seller));
        when(orderRepository.existsByUserAndSeller(buyer, seller)).thenReturn(false);

        ReviewException exception = assertThrows(ReviewException.class, () ->
                reviewService.leaveReview(2L, reviewCreateDTO, session)
        );

        assertEquals("Вы не можете оставить отзыв продавцу, у которого не покупали товар", exception.getMessage());
    }

    @Test
    void shouldGetSellerReviewsSuccessfully() {
        List<Review> reviews = List.of(review);
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(1L, seller.getLogin(), buyer.getLogin(), 5, "Great seller!", LocalDateTime.now());

        when(reviewRepository.findBySellerId(2L)).thenReturn(reviews);
        when(reviewMapper.toReviewResponseDTO(any(Review.class))).thenReturn(responseDTO);

        List<ReviewResponseDTO> result = reviewService.getSellerReviews(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
    }

    @Test
    void shouldGetSellerAverageRatingSuccessfully() {
        when(reviewRepository.getAverageRatingBySellerId(2L)).thenReturn(Optional.of(4.5));

        double avgRating = reviewService.getSellerAverageRating(2L);

        assertEquals(4.5, avgRating);
    }

    @Test
    void shouldReturnZeroWhenNoReviews() {
        when(reviewRepository.getAverageRatingBySellerId(2L)).thenReturn(Optional.empty());

        double avgRating = reviewService.getSellerAverageRating(2L);

        assertEquals(0.0, avgRating);
    }

    @Test
    void shouldDeleteReviewSuccessfully() {
        when(reviewRepository.existsById(1L)).thenReturn(true);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentReview() {
        when(reviewRepository.existsById(1L)).thenReturn(false);

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () ->
                reviewService.deleteReview(1L)
        );

        assertEquals("Отзыв с ID 1 не найден", exception.getMessage());
    }
}