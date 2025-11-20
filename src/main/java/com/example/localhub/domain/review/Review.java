package com.example.localhub.domain.review;

import com.example.localhub.domain.place.Place;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String authorId; // JWT 붙이기 전 임시

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id")
    private Place place;

    @Min(1) @Max(5)
    private int rating;

    @NotBlank
    @Column(length = 1000)
    private String content;
}

