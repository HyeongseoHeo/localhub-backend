package com.example.localhub.repository;

import com.example.localhub.domain.board.Comment;
import com.example.localhub.domain.board.CommentLike;
import com.example.localhub.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);
    Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);
}
