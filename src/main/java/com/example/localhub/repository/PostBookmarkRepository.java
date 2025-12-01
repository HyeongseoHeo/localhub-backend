package com.example.localhub.repository;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.board.PostBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
    Optional<PostBookmark> findByPostIdAndMemberId(Long postId, Long memberId);

    @Query("SELECT p FROM Post p JOIN PostBookmark pb ON p.id = pb.post.id WHERE pb.member.id = :memberId")
    Page<Post> findBookmarkedPostsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
