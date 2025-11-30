package com.example.localhub.domain.board;

import com.example.localhub.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "post_ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "member_id"}) // 한 유저는 한 게시글에 별점 1개만 가능
})
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제한 (안전성 UP)
@AllArgsConstructor // @Builder를 쓰기 위해 필요
@Builder // 객체 생성을 편리하게 하기 위해 추가
public class PostRating {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private int score; // 1~5점

    // [핵심] 점수 수정 비즈니스 로직
    // 이미 별점을 줬던 사람이 점수를 바꿀 때 사용합니다.
    public void updateScore(int score) {
        this.score = score;
    }
}