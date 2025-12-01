package com.example.localhub.repository;

import com.example.localhub.domain.board.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    Page<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    long countByPostId(Long postId);
}

