package com.islamhamada.petshop.reviewservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_id")
    private long productId;

    @Column(name = "user_id")
    private long userId;

    private String text;
    private int rating;
}
