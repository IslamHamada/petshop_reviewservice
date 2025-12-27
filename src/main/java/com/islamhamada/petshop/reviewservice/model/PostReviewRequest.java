package com.islamhamada.petshop.reviewservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewRequest {
    private String text;
    @Min(1) @Max(5)
    private int rating;
    @PositiveOrZero
    private long product_id;
    @PositiveOrZero
    private long user_id;
}
