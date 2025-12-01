package com.example.localhub.repository;

import com.example.localhub.domain.board.PostRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRatingRepository extends JpaRepository<PostRating, Long> {

    // 1. 이미 별점을 준 적이 있는지 확인 (기존 유지)
    Optional<PostRating> findByPostIdAndMemberId(Long postId, Long memberId);

    // 2. 평균 별점 계산 (기존 유지)
    @Query("SELECT AVG(r.score) FROM PostRating r WHERE r.post.id = :postId")
    Double getAverageScoreByPostId(@Param("postId") Long postId);

    // 3. [추가] 별점 참여자 수 카운트 (JPA 네이밍 규칙으로 자동 생성됨)
    Long countByPostId(Long postId);

    // 4. [추가] 별점 총합 계산 (JPQL 사용)
    @Query("SELECT SUM(r.score) FROM PostRating r WHERE r.post.id = :postId")
    Integer sumScoreByPostId(@Param("postId") Long postId);
}
