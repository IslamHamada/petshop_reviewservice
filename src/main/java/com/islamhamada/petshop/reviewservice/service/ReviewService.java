package com.islamhamada.petshop.reviewservice.service;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.model.SummarizeReivewsResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ReviewService {
    Review getReviewById(long id);
    Review postProductReview(PostReviewRequest request) throws ExecutionException, InterruptedException;
    List<Review> getReviewByProductId(long productId);
    SummarizeReivewsResponse summarizeReviewsByProductId(long productId);
    Review getReviewByProductIdAndUserId(long productId, long userId);
}
