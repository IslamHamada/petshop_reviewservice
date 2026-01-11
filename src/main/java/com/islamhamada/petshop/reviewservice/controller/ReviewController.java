package com.islamhamada.petshop.reviewservice.controller;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @PreAuthorize("hasAnyRole('Admin')")
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PositiveOrZero @PathVariable long id){
        Review review = reviewService.getReviewById(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @PostMapping
    public ResponseEntity<Review> postProductReview(@Valid @RequestBody PostReviewRequest request){
        Review review = reviewService.postProductReview(request);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @GetMapping("/product/{product_id}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PositiveOrZero @PathVariable long product_id) {
        List<Review> reviews =  reviewService.getReviewByProductId(product_id);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @GetMapping("/product/user/{productId}/{userId}")
    public ResponseEntity<Review> getReviewByProductIdAndUserId(@PositiveOrZero @PathVariable long productId, @PositiveOrZero @PathVariable long userId){
        Review review = reviewService.getReviewByProductIdAndUserId(productId, userId);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }
}
