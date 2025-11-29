package com.example.localhub.controller;

import com.example.localhub.domain.member.Member;
import com.example.localhub.dto.member.MemberLoginRequest;
import com.example.localhub.dto.member.MemberSignupRequest;
import com.example.localhub.service.MemberService;
import com.example.localhub.security.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

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
            Map<String, Object> res = new HashMap<>();
            res.put("error", "로그인 상태가 아닙니다.");
            return res;
        }

        Long id = Long.parseLong(authentication.getName());
        return memberService.getMember(id);
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
}


