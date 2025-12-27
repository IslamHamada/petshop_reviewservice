package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;

import java.util.List;

public interface ReviewService {
    Review getReviewById(long id);
    Review postProductReview(PostReviewRequest request);
    List<Review> getReviewByProductId(long productId);
}
