package com.example.localhub.repository;

import com.example.localhub.domain.member.TravelNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelNoteRepository extends JpaRepository<TravelNote, Long> {
    List<TravelNote> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
}
