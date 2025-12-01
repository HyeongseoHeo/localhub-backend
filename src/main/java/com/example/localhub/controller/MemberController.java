package com.example.localhub.controller;

import com.example.localhub.domain.member.Member;
import com.example.localhub.domain.member.TravelNote;
import com.example.localhub.dto.board.PostResponse;
import com.example.localhub.dto.comment.CommentResponse;
import com.example.localhub.dto.member.MemberLoginRequest;
import com.example.localhub.dto.member.MemberSignupRequest;
import com.example.localhub.security.details.MemberDetailsService;
import com.example.localhub.service.CommentService;
import com.example.localhub.service.MemberService;
import com.example.localhub.security.JwtTokenProvider;
import com.example.localhub.service.PostService;
import com.example.localhub.service.TravelNoteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PostService postService;
    private final CommentService commentService;
    private final TravelNoteService travelNoteService;

    // 회원가입
    @PostMapping("/signup")
    public Member signup(@RequestBody MemberSignupRequest req) {
        return memberService.signup(req.getEmail(), req.getPassword(), req.getNickname());
    }

    // 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody MemberLoginRequest req) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                req.getEmail(), req.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtTokenProvider.createToken(authentication);

        // NOTE: 이메일로 회원 정보를 다시 조회하는 메서드가 MemberService에 필요합니다.
        Member member = memberService.getMemberByEmail(req.getEmail());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "로그인 성공");
        result.put("memberId", member.getId());
        result.put("email", member.getEmail());
        result.put("nickname", member.getNickname());
        result.put("role", member.getRole());
        result.put("isBusiness", member.isManager());
        result.put("cleanbotOn", member.isCleanbotOn());
        result.put("token", jwtToken);

        return result;
    }

    // 로그인된 사용자 정보 조회
    @GetMapping("/me")
    public Object me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        Long id = Long.parseLong(authentication.getName());
        Member member = memberService.getMember(id);

        Map<String, Object> result = new HashMap<>();
        result.put("id", member.getId());
        result.put("email", member.getEmail());
        result.put("nickname", member.getNickname());
        result.put("role", member.getRole());
        result.put("manager", member.isManager());       // isManager() 메서드 확인 필요
        result.put("cleanbotOn", member.isCleanbotOn()); // isCleanbotOn() 확인 필요

        return result;
    }

    //로그아웃
    @PostMapping("/logout")
    public Map<String, String> logout() {
        Map<String, String> res = new HashMap<>();
        res.put("message", "로그아웃 성공");
        // 클라이언트에게 토큰 삭제를 위임
        return res;
    }

    //멤버 탈퇴
    @DeleteMapping("/delete")
    public Map<String, String> delete() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }
        Long memberId = Long.parseLong(authentication.getName());

        memberService.deleteMember(memberId);

        Map<String, String> res = new HashMap<>();
        res.put("message", "회원 탈퇴 완료");
        return res;
    }

    // 클린봇 설정 변경
    @PutMapping("/{id}/cleanbot")
    public Map<String, String> toggleCleanbot(@PathVariable Long id) {
        memberService.toggleCleanbot(id);

        Map<String, String> res = new HashMap<>();
        res.put("message", "클린봇 설정이 변경되었습니다.");
        return res;
    }

    // 쓴 글 목록
    @GetMapping("/me/posts")
    public Page<PostResponse> getMyPosts(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            Pageable pageable
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (memberId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }

        return postService.getMyPosts(memberId, pageable);
    }

    // 작성한 댓글
    @GetMapping("/me/comments")
    public Page<CommentResponse> getMyComments(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            Pageable pageable
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (memberId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }

        return commentService.getMyComments(memberDetails.getMember().getId(), pageable);
    }

    // 여행 기록 조회
    @GetMapping("/me/travel-notes")
    public List<TravelNote> getMyNotes(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (memberId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }

        return travelNoteService.getMyNotes(memberDetails.getMember().getId());
    }

    // 여행 기록 작성
    @PostMapping("/me/travel-notes")
    public void createNote(
            @RequestBody Map<String, String> body, @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (memberId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }
        travelNoteService.createNote(memberDetails.getMember().getId(), body.get("content"));
    }

    // 좋아요 한 글 목록
    @GetMapping("/me/likes")
    public Page<PostResponse> getMyLikedPosts(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            Pageable pageable
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (memberId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }

        return postService.getMyLikedPosts(memberId, pageable);
    }

    @GetMapping("/me/bookmarks")
    public Page<PostResponse> getMyBookmarkedPosts(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            Pageable pageable
    ) {
        Long memberId = (memberDetails != null) ? memberDetails.getMember().getId() : null;

        if (memberId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }

        return postService.getMyBookmarkedPosts(memberId, pageable);
    }

    // 사용자가 가장 자주 방문한 지역 조회
    @GetMapping("/{memberId}/top-region")
    public Map<String, String> getTopRegion(@PathVariable Long memberId) {
        String topRegion = postService.getTopRegion(memberId);

        Map<String, String> response = new HashMap<>();
        response.put("region", topRegion);
        return response;
    }
}


