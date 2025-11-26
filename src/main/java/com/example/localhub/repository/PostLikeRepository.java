package com.example.localhub.repository;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.board.PostLike;
import com.example.localhub.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndMember(Post post, Member member);

    void deleteByPostAndMember(Post post, Member member);

    long countByPost(Post post);
}
