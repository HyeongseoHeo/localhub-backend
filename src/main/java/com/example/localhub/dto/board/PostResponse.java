package com.example.localhub.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Long id;

    // SNS 탭용 제목 (추가됨)
    private String title;

    private String region;
    private String author;
    private String authorId;
    private String role;

    private String content;
    private LocalDateTime timestamp;
    private LocalDateTime updatedAt;

    private int views;
    private int likesCount;
    private boolean liked;
    private long commentsCount;

    private double rating;
    private int ratingCount;
    private int totalRatingScore;

    private boolean ad;
    private List<String> tags;
    private List<String> images;

    private PlaceResponse place;

    private boolean bookmarked;

    private boolean isSns;
}



