package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.exception.ReviewException;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.model.SummarizeReivewsResponse;
import com.islamhamada.petshop.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;
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
        public void success_new() {
            PostReviewRequest request = getMockPostReviewRequest();
            when(reviewRepository.findByProductIdAndUserId(anyLong(), anyLong()))
                    .thenReturn(Optional.empty());
            when(reviewRepository.save(any()))
                    .thenReturn(Review.builder()
                            .text(request.getText())
                            .rating(request.getRating())
                            .productId(request.getProductId())
                            .userId(request.getUserId())
                            .build());
            Review rv = reviewService.postProductReview(request);

            verify(reviewRepository, times(1))
                    .findByProductIdAndUserId(anyLong(), anyLong());
            verify(reviewRepository, times(1))
                    .save(any());

            assertEquals(request.getProductId(), rv.getProductId());
            assertEquals(request.getRating(), rv.getRating());
            assertEquals(request.getText(), rv.getText());
            assertEquals(request.getUserId(), rv.getUserId());
        }

        @Test
        public void success_with_old() {
            PostReviewRequest request = getMockPostReviewRequest();
            when(reviewRepository.findByProductIdAndUserId(request.getProductId(), request.getUserId()))
                    .thenReturn(Optional.of(getMockReview3()));
            when(reviewRepository.save(any()))
                    .thenReturn(Review.builder()
                            .userId(getMockPostReviewRequest().getUserId())
                            .productId(getMockPostReviewRequest().getProductId())
                            .text(request.getText())
                            .rating(request.getRating())
                            .build());
            Review rv = reviewService.postProductReview(request);
            verify(reviewRepository, times(1))
                    .findByProductIdAndUserId(anyLong(), anyLong());
            verify(reviewRepository, times(1))
                    .save(any());

            assertEquals(request.getProductId(), rv.getProductId());
            assertEquals(request.getRating(), rv.getRating());
            assertEquals(request.getText(), rv.getText());
            assertEquals(request.getUserId(), rv.getUserId());
        }

        private PostReviewRequest getMockPostReviewRequest() {
            return PostReviewRequest.builder()
                    .productId(2)
                    .userId(2)
                    .text("text")
                    .rating(3)
                    .build();
        }
    }

    @Nested
    class getReviewByProductId {

        @Test
        void success() {
            long productId = 1;
            when(reviewRepository.findByProductId(anyLong()))
                    .thenReturn(List.of(getMockReview(),
                            getMockReview2()));
            List<Review> reviews = reviewService.getReviewByProductId(productId);
            verify(reviewRepository, times(1))
                    .findByProductId(anyLong());
            assertEquals(productId, reviews.get(0).getProductId());
            assertEquals(productId, reviews.get(1).getProductId());
        }
    }

    @Nested
    class summarizeReviewsByProductId {

        @Test
        void success() {
            long productId = 1;
            when(reviewRepository.findByProductId(anyLong()))
                    .thenReturn(List.of(getMockReview(), getMockReview2()));
            SummarizeReivewsResponse summary = reviewService.summarizeReviewsByProductId(productId);
            verify(reviewRepository, times(1))
                    .findByProductId(anyLong());
            assertEquals(2, summary.getCount());
            double rating = (getMockReview().getRating() + getMockReview2().getRating()) / 2.0;
            assertEquals(rating, summary.getRating());
        }
    }

    @Nested
    class getReviewByProductIdAndUserId {
        @Test
        void success_exists() {
            long productId = 1;
            long userId = 1;
            when(reviewRepository.findByProductIdAndUserId(anyLong(), anyLong()))
                    .thenReturn(Optional.of(getMockReview()));
            Review review = reviewService.getReviewByProductIdAndUserId(productId, userId);
            verify(reviewRepository, times(1))
                    .findByProductIdAndUserId(anyLong(), anyLong());
            assertEquals(productId, review.getProductId());
            assertEquals(userId, review.getUserId());
        }
        @Test
        void success_not_exists() {
            long productId = 1;
            long userId = 1;
            when(reviewRepository.findByProductIdAndUserId(anyLong(), anyLong()))
                    .thenReturn(Optional.ofNullable(null));
            Review review = reviewService.getReviewByProductIdAndUserId(productId, userId);
            verify(reviewRepository, times(1))
                    .findByProductIdAndUserId(anyLong(), anyLong());
            assertEquals(null, review);
        }
    }

    Review getMockReview() {
        return Review.builder()
                .id(1)
                .productId(1)
                .userId(1)
                .text("review text")
                .rating(3)
                .build();
    }

    public Review getMockReview2() {
        return Review.builder()
                .id(2)
                .productId(1)
                .userId(2)
                .text("review text2")
                .rating(4)
                .build();
    }

    public Review getMockReview3() {
        return Review.builder()
                .id(2)
                .productId(2)
                .userId(2)
                .text("review text3")
                .rating(1)
                .build();
    }
}
