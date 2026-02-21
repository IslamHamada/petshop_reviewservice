package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.contracts.model.KafkaUserMessage;
import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.exception.ReviewException;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.model.SummarizeReivewsResponse;
import com.islamhamada.petshop.reviewservice.repository.ReviewRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    KafkaTemplate<String, KafkaUserMessage> kafkaTemplate;

    @Override
    public Review getReviewById(long id) {
        log.info("fetching review by id: " + id);
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new ReviewException(
                        "Review with id: " + id + " not found",
                        "NOT_FOUND",
                        HttpStatus.NOT_FOUND)
        );
        log.info("Review successfully fetched");
        return review;
    }

    @Override
    public Review postProductReview(PostReviewRequest request) throws ExecutionException, InterruptedException {
        log.info("Posting Product Review with text: " + request.getText()
            + " and rating: " + request.getRating()
            + " by user with id: " + request.getUserId()
            + " for product with id: " + request.getProductId());
        Optional<Review> old_review = reviewRepository.findByProductIdAndUserId(request.getProductId(), request.getUserId());
        Review review;
        if(old_review.isPresent()) {
            review = old_review.get();
            review.setRating(request.getRating());
            review.setText(request.getText());
            System.out.println(kafkaTemplate.send("notification", KafkaUserMessage.builder()
                    .userId(request.getUserId())
                    .message("Edited a product review")
                    .build()).get());
        } else {
            kafkaTemplate.send("notification", KafkaUserMessage.builder()
                    .userId(request.getUserId())
                    .message("Reviewed a product successfully")
                    .build());
            review = Review.builder()
                    .text(request.getText())
                    .rating(request.getRating())
                    .productId(request.getProductId())
                    .userId(request.getUserId())
                    .build();
        }
        review = reviewRepository.save(review);
        log.info("review successfully posted");
        return review;
    }

    @Override
    public List<Review> getReviewByProductId(long productId) {
        log.info("Get reviews of product with id: " + productId);
        List<Review> reviews = reviewRepository.findByProductId(productId);
        log.info("Reviews successfully fetched");
        return reviews;
    }

    @Override
    public SummarizeReivewsResponse summarizeReviewsByProductId(long product_id) {
        List<Review> reviews = reviewRepository.findByProductId(product_id);
        if(reviews.isEmpty()) return new SummarizeReivewsResponse();
        SummarizeReivewsResponse summary = new SummarizeReivewsResponse();
        reviews.forEach(review -> {
            summary.setCount(summary.getCount() + 1);
            summary.setRating(summary.getRating() + review.getRating());
        });
        summary.setRating(summary.getRating() / summary.getCount());
        return summary;
    }

    @Override
    public Review getReviewByProductIdAndUserId(long productId, long userId) {
        Optional<Review> review = reviewRepository.findByProductIdAndUserId(productId, userId);
        return review.orElse(null);
    }
}
