package com.example.localhub.controller;

import com.example.localhub.domain.member.Member;
import com.example.localhub.dto.member.MemberLoginRequest;
import com.example.localhub.dto.member.MemberSignupRequest;
import com.example.localhub.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public Member signup(@RequestBody MemberSignupRequest req) {
        return memberService.signup(req.getEmail(), req.getPassword(), req.getNickname());
    }

    // 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody MemberLoginRequest req, HttpSession session) {
        Member member = memberService.login(req.getEmail(), req.getPassword());

        // 세션에 회원 id 저장
        session.setAttribute("memberId", member.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "로그인 성공");
        result.put("memberId", member.getId());
        result.put("email", member.getEmail());
        result.put("nickname", member.getNickname());
        result.put("role", member.getRole());
        result.put("isBusiness", member.isManager());
        result.put("cleanbotOn", member.isCleanbotOn());

        return result;
    }

    // 로그인된 사용자 정보 조회
    @GetMapping("/me")
    public Object me(HttpSession session) {
        Long id = (Long) session.getAttribute("memberId");
        if (id == null) {
            Map<String, Object> res = new HashMap<>();
            res.put("error", "로그인 상태가 아닙니다.");
            return res;
        }
        return memberService.getMember(id);
    }

    //로그아웃
    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();

        Map<String, String> res = new HashMap<>();
        res.put("message", "로그아웃 성공");

        return res;
    }

    //멤버 탈퇴
    @DeleteMapping("/delete")
    public Map<String, String> delete(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            throw new RuntimeException("로그인 상태가 아닙니다.");
        }

        memberService.deleteMember(memberId);
        session.invalidate();

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
}


