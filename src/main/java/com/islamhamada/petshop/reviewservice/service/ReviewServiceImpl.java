package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.exception.ReviewException;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public Review getReviewById(long id) {
        return reviewRepository.findById(id).orElseThrow(
                () -> new ReviewException(
                        "Review with id: " + id + " not found",
                        "NOT_FOUND",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public Review postProductReview(PostReviewRequest request) {
        Review review = Review.builder()
                .text(request.getText())
                .rating(request.getRating())
                .productId(request.getProduct_id())
                .userId(request.getUser_id())
                .build();
        return reviewRepository.save(review);
    }
}
