package com.example.localhub.repository;

import com.example.localhub.domain.board.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByRegion(String region, Pageable pageable);
    Page<Post> findDistinctByTagsIn(List<String> tags, Pageable pageable);
    // 특정 작성자가 특정 지역에 쓴 글 개수 카운트
    Long countByRegionAndAuthorId(String region, Long authorId);
    // [내가 쓴 글] 작성자 ID로 페이징 조회
    Page<Post> findAllByAuthorId(Long authorId, Pageable pageable);
    // [내가 좋아요 한 글] JOIN을 이용해 좋아요 테이블을 거쳐서 Post 가져오기
    @Query("SELECT p FROM Post p JOIN p.postLikes pl WHERE pl.member.id = :memberId")
    Page<Post> findLikedPostsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
    // [마이 페이지] 특정 멤버가 작성한 게시글 중 가장 많이 등장한 지역(Region) 1개 조회
    @Query("SELECT p.region FROM Post p WHERE p.author.id = :memberId GROUP BY p.region ORDER BY COUNT(p) DESC LIMIT 1")
    String findTopRegionByAuthorId(@Param("memberId") Long memberId);
}




