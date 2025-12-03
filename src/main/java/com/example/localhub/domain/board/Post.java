package com.example.localhub.domain.board;

import com.example.localhub.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // 지역 코드
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

    //댓글 수
    @Column(name = "comments_count", nullable = false, columnDefinition = "integer default 0")
    private int commentsCount = 0;

    // 좋아요 수
    @Column(name = "likes_count", nullable = false)
    private int likes = 0;

    // 평균 별점 (DB에 저장할 필드 추가)
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

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
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    // 지도 정보
    private String address;
    private Double latitude;
    private Double longitude;

    // SNS
    private String title;

    @Column(name = "is_sns", nullable = false)
    private boolean isSns = false;

    // 이미지 URL 리스트
    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 이름 충돌 방지
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostRating> ratings = new ArrayList<>();

    // --- 이벤트 및 편의 메서드 ---

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 평균 별점 업데이트 메서드 (Service에서 호출)
    public void updateAverageRating(double averageRating) {
        // 소수점 한 자리까지 반올림 (예: 4.666 -> 4.7)
        this.averageRating = Math.round(averageRating * 10.0) / 10.0;
    }
}


