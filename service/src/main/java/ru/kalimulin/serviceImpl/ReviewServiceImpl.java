package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.reviewExceptions.ReviewException;
import ru.kalimulin.customExceptions.reviewExceptions.ReviewNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.reviewDTO.ReviewCreateDTO;
import ru.kalimulin.dto.reviewDTO.ReviewResponseDTO;
import ru.kalimulin.mappers.reviewMapper.ReviewMapper;
import ru.kalimulin.models.Review;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.OrderRepository;
import ru.kalimulin.repositories.ReviewRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.service.ReviewService;
import ru.kalimulin.util.SessionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final OrderRepository orderRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository,
                             ReviewMapper reviewMapper, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional
    @Override
    public ReviewResponseDTO leaveReview(Long sellerId, ReviewCreateDTO reviewCreateDTO, HttpSession session) {
        logger.info("Попытка оставить отзыв");
        String userLogin = SessionUtils.getUserLogin(session);
        User buyer = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Покупатель с логином " + userLogin + " не найден"));
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new UserNotFoundException("Продавец с id " + sellerId + " не найден"));

        boolean hasPurchasedFromSeller = orderRepository.existsByUserAndSeller(buyer, seller);

        if (!hasPurchasedFromSeller) {
            throw new ReviewException("Вы не можете оставить отзыв продавцу, у которого не покупали товар");
        }

        Review review = Review.builder()
                .buyer(buyer)
                .seller(seller)
                .rating(reviewCreateDTO.getRating())
                .comment(reviewCreateDTO.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        review = reviewRepository.save(review);

        logger.info("Отзыв успешно оставлен");
        return reviewMapper.toReviewResponseDTO(review);
    }

    @Override
    public List<ReviewResponseDTO> getSellerReviews(Long sellerId) {
        logger.info("Получение отзывов продавца {}", sellerId);
        List<Review> reviews = reviewRepository.findBySellerId(sellerId);
        return reviews.stream().map(reviewMapper::toReviewResponseDTO).toList();
    }

    @Override
    public double getSellerAverageRating(Long sellerId) {
        logger.info("Получение среднего рейтинга продавца");
        return reviewRepository.getAverageRatingBySellerId(sellerId).orElse(0.0);
    }

    @Transactional
    @Override
    public void deleteReview(Long id) {
        logger.info("Удаление отзыва {}", id);
        if (!reviewRepository.existsById(id)) {
            logger.warn("Отзыв не найден");
            throw new ReviewNotFoundException("Отзыв с ID " + id + " не найден");
        }
        logger.info("Отзыв удален");
        reviewRepository.deleteById(id);
    }
}