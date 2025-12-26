package com.islamhamada.petshop.reviewservice.repository;

import com.islamhamada.petshop.reviewservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
