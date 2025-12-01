package com.example.localhub.service;

import com.example.localhub.domain.member.Member;
import com.example.localhub.domain.member.TravelNote;
import com.example.localhub.repository.MemberRepository;
import com.example.localhub.repository.TravelNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelNoteService {
    private final TravelNoteRepository travelNoteRepository;
    private final MemberRepository memberRepository;

    // 조회
    public List<TravelNote> getMyNotes(Long memberId) {
        return travelNoteRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 생성
    public void createNote(Long memberId, String content) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        travelNoteRepository.save(TravelNote.builder().member(member).content(content).build());
    }

    // 삭제
    public void deleteNote(Long noteId, Long memberId) {
        TravelNote note = travelNoteRepository.findById(noteId).orElseThrow();
        if (!note.getMember().getId().equals(memberId)) throw new RuntimeException("권한 없음");
        travelNoteRepository.delete(note);
    }
}
