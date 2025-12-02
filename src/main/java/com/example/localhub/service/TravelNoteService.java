package com.example.localhub.service;

import com.example.localhub.domain.member.Member;
import com.example.localhub.domain.member.TravelNote;
import com.example.localhub.dto.member.TravelNoteRequest;
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
    @Transactional(readOnly = true)
    public List<TravelNote> getMyNotes(Long memberId) {
        return travelNoteRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 생성
    public void createNote(Long memberId, TravelNoteRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        TravelNote note = TravelNote.builder()
                .member(member)
                .title(request.getTitle())
                .place(request.getPlace())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .content(request.getContent())
                .build();

        travelNoteRepository.save(note);
    }

    // 삭제
    public void deleteNote(Long noteId, Long memberId) {
        TravelNote note = travelNoteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("메모 없음"));

        if (!note.getMember().getId().equals(memberId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        travelNoteRepository.delete(note);
    }
}
