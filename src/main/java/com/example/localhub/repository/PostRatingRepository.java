package com.example.localhub.repository;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.board.PostRating;
import com.example.localhub.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PostRatingRepository extends JpaRepository<PostRating, Long> {
    Optional<PostRating> findByPostIdAndMemberId(Long postId, Long memberId);

    @Query("SELECT AVG(r.score) FROM PostRating r WHERE r.post.id = :postId")
    Double getAverageScoreByPostId(@Param("postId") Long postId);
}
