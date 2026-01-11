package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.exception.ReviewException;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.repository.ReviewRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    ReviewRepository reviewRepository;

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
    public Review postProductReview(PostReviewRequest request) {
        log.info("Posting Product Review with text: " + request.getText()
            + " and rating: " + request.getRating()
            + " by user with id: " + request.getUserId()
            + " for product with id: " + request.getProductId());
                .text(request.getText())
                .rating(request.getRating())
                .productId(request.getProduct_id())
                .userId(request.getUser_id())
                .build();
        reviewRepository.save(review);
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
    public Review getReviewByProductIdAndUserId(long productId, long userId) {
        Optional<Review> review = reviewRepository.findByProductIdAndUserId(productId, userId);
        return review.orElse(null);
    }
}
