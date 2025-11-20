package com.example.localhub.controller;

import com.example.localhub.domain.review.Review;
import com.example.localhub.service.ReviewService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    public record CreateReq(@NotBlank String authorId,
                            Long placeId,
                            @Min(1) @Max(5) int rating,
                            @NotBlank String content) {}

    @PostMapping
    public Review create(@RequestBody CreateReq req){
        return reviewService.create(req.placeId(), req.authorId(), req.rating(), req.content());
    }

    @GetMapping("/place/{placeId}")
    public Page<Review> listByPlace(@PathVariable Long placeId,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size){
        return reviewService.listByPlace(placeId, page, size);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){ reviewService.delete(id); }
}

