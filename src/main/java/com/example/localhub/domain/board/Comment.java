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
@Table(name = "comments")
@Getter @Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 게시글 댓글인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean malicious = false;

    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likes = 0;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 익명 여부
    @Column(nullable = false)
    private boolean anonymous = false;

    @Column(nullable = false)
    private int likesCount = 0;

}

