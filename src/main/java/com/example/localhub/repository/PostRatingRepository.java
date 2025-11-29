package com.example.localhub.repository;

import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.board.PostRating;
import com.example.localhub.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostRatingRepository extends JpaRepository<PostRating, Long> {
    Optional<PostRating> findByPostAndMember(Post post, Member member);
}
