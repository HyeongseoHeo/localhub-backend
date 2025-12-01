package com.example.localhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.localhub.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
}


