package com.example.localhub.controller;

import com.example.localhub.domain.member.TravelNote;
import com.example.localhub.security.details.MemberDetailsService;
import com.example.localhub.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/travel-notes")
@RequiredArgsConstructor
public class TravelNoteController {

    private final TravelNoteService travelNoteService;

    // 특정 사용자 여행 메모 목록 조회
    @GetMapping
    public List<TravelNote> getNotes(@RequestParam Long memberId) {
        return travelNoteService.getMyNotes(memberId);
    }

    // 여행 메모 생성
    @PostMapping
    public void createNote(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        travelNoteService.createNote(memberDetails.getMember().getId(), body.get("content"));
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
