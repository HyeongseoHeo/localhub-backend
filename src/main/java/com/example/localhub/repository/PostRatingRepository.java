package com.example.localhub.repository;

import com.example.localhub.domain.board.PostRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRatingRepository extends JpaRepository<PostRating, Long> {

    Optional<PostRating> findByPostIdAndMemberId(Long postId, Long memberId);

    @Query("SELECT AVG(r.score) FROM PostRating r WHERE r.post.id = :postId")
    Double getAverageScoreByPostId(@Param("postId") Long postId);

    Long countByPostId(Long postId);

    @Query("SELECT SUM(r.score) FROM PostRating r WHERE r.post.id = :postId")
    Integer sumScoreByPostId(@Param("postId") Long postId);
}
