package com.example.localhub.repository;

import com.example.localhub.domain.board.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByAdFalse(Pageable pageable);

    // 추천용
    List<Post> findTop5ByAdFalseOrderByViewsDescLikesCountDesc();

    // 지역 필터링 추가
    Page<Post> findByRegionAndAdFalse(String region, Pageable pageable);
}



