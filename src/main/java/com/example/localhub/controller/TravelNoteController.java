package com.example.localhub.controller;

import com.example.localhub.domain.member.TravelNote;
import com.example.localhub.dto.member.TravelNoteRequest;
import com.example.localhub.dto.member.TravelNoteResponse;
import com.example.localhub.security.details.MemberDetailsService;
import com.example.localhub.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/travel-notes")
@RequiredArgsConstructor
public class TravelNoteController {

    private final TravelNoteService travelNoteService;

    // 특정 사용자 여행 메모 목록 조회
    @GetMapping
    public List<TravelNoteResponse> getNotes(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        List<TravelNote> notes = travelNoteService.getMyNotes(memberId);

        // DTO로 변환하여 반환 (순환 참조 방지)
        return notes.stream()
                .map(note -> new TravelNoteResponse(
                        note.getId(),
                        note.getTitle(),
                        note.getPlace(),
                        note.getStartDate(),
                        note.getEndDate(),
                        note.getContent(),
                        note.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 여행 메모 생성
    @PostMapping
    public void createNote(
            @RequestBody TravelNoteRequest request, // [수정] Map -> DTO로 변경
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = memberDetails.getMember().getId();
        travelNoteService.createNote(memberId, request);
    }

    // 여행 메모 삭제
    @DeleteMapping("/{noteId}")
    public void deleteNote(
            @PathVariable Long noteId,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        travelNoteService.deleteNote(noteId, memberDetails.getMember().getId());
    }
}
