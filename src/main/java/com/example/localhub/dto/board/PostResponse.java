package com.example.localhub.dto.board;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponse {

    private Long id;

    private String region;       // 지역 코드
    private String author;       // 작성자 닉네임
    private String authorId;     // 작성자 ID

    private String content;      // 게시글 내용
    private LocalDateTime timestamp; // created_at
    private LocalDateTime updatedAt;

    private int views;           // 조회수
    private int likesCount;      // 좋아요 수
    private long commentsCount;  // 댓글 수

    private double rating;       // 평균 별점 (계산값)
    private int ratingCount;     // 참여자 수
    private int totalRatingScore;// 별점 총합

    private boolean ad;          // 광고 여부
    private List<String> keywords; // 태그
}


