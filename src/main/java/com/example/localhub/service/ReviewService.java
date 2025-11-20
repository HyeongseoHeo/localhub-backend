package com.example.localhub.service;

import com.example.localhub.domain.review.Review;
import com.example.localhub.domain.place.Place;
import com.example.localhub.repository.PlaceRepository;
import com.example.localhub.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepo;
    private final PlaceRepository placeRepo;

    public Review create(Long placeId, String authorId, int rating, String content){
        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("place not found"));
        return reviewRepo.save(Review.builder()
                .place(place).authorId(authorId).rating(rating).content(content).build());
    }

    public Page<Review> listByPlace(Long placeId, int page, int size){
        Place place = placeRepo.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("place not found"));
        return reviewRepo.findByPlace(place, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public void delete(Long id){ reviewRepo.deleteById(id); }
}

