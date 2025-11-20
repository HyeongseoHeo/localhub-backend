package com.example.localhub.repository;

import com.example.localhub.domain.review.Review;
import com.example.localhub.domain.place.Place;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByPlace(Place place, Pageable pageable);
}

