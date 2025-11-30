package com.example.localhub.repository;

import com.example.localhub.domain.board.Comment;
import com.example.localhub.domain.board.CommentLike;
import com.example.localhub.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentAndMember(Comment comment, Member member);
    void deleteByCommentAndMember(Comment comment, Member member);
}
