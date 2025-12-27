package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.exception.ReviewException;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewServiceImplTest {

    @Mock
    ReviewRepository reviewRepository;

    @InjectMocks
    ReviewService reviewService = new ReviewServiceImpl();

    @Nested
    class getReviewById {

        @Test
        public void success() {
            Review review = getMockReview();

            when(reviewRepository.findById(anyLong()))
                    .thenReturn(Optional.ofNullable(review));

            Review rv = reviewService.getReviewById(review.getId());

            verify(reviewRepository, times(1))
                    .findById(anyLong());

            assertEquals(review.getId(), rv.getId());
            assertEquals(review.getText(), rv.getText());
            assertEquals(review.getRating(), rv.getRating());
            assertEquals(review.getUserId(), rv.getUserId());
            assertEquals(review.getProductId(), rv.getProductId());
        }

        @Test
        public void failure() {
            int review_id = 1;
            when(reviewRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            ReviewException exception = assertThrows(
                    ReviewException.class,
                    () -> reviewService.getReviewById(review_id)
            );

            verify(reviewRepository, times(1))
                    .findById(anyLong());

            assertEquals(
                    "Review with id: " + review_id + " not found",
                    exception.getMessage()
            );

            assertEquals("REVIEW_NOT_FOUND", exception.getError_code());
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        }
    }

    @Nested
    class postProductReview {

        @Test
        public void success() {
            PostReviewRequest request = getMockPostReviewRequest();

            Review rv = reviewService.postProductReview(request);

            verify(reviewRepository, times(1))
                    .save(any());

            assertEquals(request.getProduct_id(), rv.getProductId());
            assertEquals(request.getRating(), rv.getRating());
            assertEquals(request.getText(), rv.getText());
            assertEquals(request.getUser_id(), rv.getUserId());
        }

        private PostReviewRequest getMockPostReviewRequest() {
            return PostReviewRequest.builder()
                    .product_id(2)
                    .user_id(2)
                    .text("text")
                    .rating(3)
                    .build();
        }
    }

    public Review getMockReview() {
        return Review.builder()
                .id(1)
                .productId(1)
                .userId(1)
                .text("review text")
                .rating(3)
                .build();
    }
}
