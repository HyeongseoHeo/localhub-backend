package com.example.localhub.domain.board;

import com.example.localhub.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    // 지역 코드 (예: "SEOUL", "BUSAN")
    @Column(nullable = false, length = 10)
    private String region;

    // 게시글 내용
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 수정 시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 조회수
    @Column(nullable = false)
    private int views = 0;

    // 좋아요 수
    @Column(name = "likes_count", nullable = false)
    private int likesCount = 0;

    // 댓글 수 (캐싱용)
    @Column(name = "comments_count", nullable = false)
    private int commentsCount = 0;

    // 별점 총합
    @Column(name = "total_rating_score", nullable = false)
    private int totalRatingScore = 0;

    // 별점 참여자 수
    @Column(name = "rating_count", nullable = false)
    private int ratingCount = 0;

    // 광고 여부
    @Column(name = "is_ad", nullable = false)
    private boolean ad = false;

    // 태그
    @ElementCollection
    @CollectionTable(name = "post_keywords", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> keywords = new ArrayList<>();


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null; // 처음에는 수정 시간 없음
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 별점 평균 계산 (필드가 아니라 계산값)
    @Transient
    public double getRating() {
        return ratingCount == 0 ? 0.0 : (double) totalRatingScore / ratingCount;
    }
}


