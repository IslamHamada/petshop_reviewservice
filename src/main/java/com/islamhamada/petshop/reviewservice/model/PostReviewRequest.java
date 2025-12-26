package com.islamhamada.petshop.reviewservice.model;

import lombok.Data;

@Data
public class PostReviewRequest {
    private String text;
    private int rating;
    private long product_id;
}
