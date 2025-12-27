package com.islamhamada.petshop.reviewservice.controller;

import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PositiveOrZero @PathVariable long id){
        Review review = reviewService.getReviewById(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Review> postProductReview(@Valid @RequestBody PostReviewRequest request){
        Review review = reviewService.postProductReview(request);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }
}
