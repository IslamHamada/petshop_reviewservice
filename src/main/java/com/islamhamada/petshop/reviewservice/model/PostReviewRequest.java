package com.islamhamada.petshop.reviewservice.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewRequest {
    @Size(max = 255)
    private String text;
    @Min(1) @Max(5)
    private int rating;
    @PositiveOrZero
    private long productId;
    @PositiveOrZero
    private long userId;
}
