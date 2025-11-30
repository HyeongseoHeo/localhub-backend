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

    List<Post> findTop5ByAdFalseOrderByViewsDescLikesDesc();
}




