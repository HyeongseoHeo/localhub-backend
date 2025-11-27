package com.example.localhub.repository;

import com.example.localhub.domain.board.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 전체 목록 (ad 필터링 X) → 기본 제공 findAll(pageable) 사용

    // 지역 필터링 (ad 상관없이 모두)
    Page<Post> findByRegion(String region, Pageable pageable);

    Page<Post> findDistinctByTagsIn(List<String> tags, Pageable pageable);
    // 추천 게시글은 그대로 (광고 제외)
    List<Post> findTop5ByAdFalseOrderByViewsDescLikesCountDesc();
}




