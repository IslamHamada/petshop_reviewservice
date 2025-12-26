package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;

public interface ReviewService {
    Review getReviewById(long id);
    Review postProductReview(PostReviewRequest request);
}
