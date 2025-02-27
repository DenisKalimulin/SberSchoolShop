package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.reviewDTO.ReviewCreateDTO;
import ru.kalimulin.dto.reviewDTO.ReviewResponseDTO;
import ru.kalimulin.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/shop/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/leave/{sellerId}")
    public ResponseEntity<ReviewResponseDTO> leaveReview(@PathVariable Long sellerId,
                                                         @Valid @RequestBody ReviewCreateDTO reviewCreateDTO,
                                                         HttpSession session) {
        ReviewResponseDTO reviewResponseDTO = reviewService.leaveReview(sellerId, reviewCreateDTO, session);
        return ResponseEntity.ok(reviewResponseDTO);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<List<ReviewResponseDTO>> getSellerReviews(@PathVariable Long sellerId) {
        List<ReviewResponseDTO> review = reviewService.getSellerReviews(sellerId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/average-rating/{sellerId}")
    public ResponseEntity<Double> getSellerAverageRating(@PathVariable Long sellerId) {
        Double rating = reviewService.getSellerAverageRating(sellerId);

        return ResponseEntity.ok(rating);
    }
}