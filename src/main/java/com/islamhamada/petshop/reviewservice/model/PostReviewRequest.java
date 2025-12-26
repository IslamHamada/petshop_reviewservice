package com.islamhamada.petshop.reviewservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PostReviewRequest {
    private String text;
    @Min(1) @Max(5)
    private int rating;
    @PositiveOrZero
    private long product_id;
}
