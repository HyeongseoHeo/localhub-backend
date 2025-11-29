package com.example.localhub.service;

import com.example.localhub.domain.member.Member;
import com.example.localhub.domain.member.Role;
import com.example.localhub.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$*])[a-zA-Z0-9!@#$*]{8,49}$";

    // 회원가입
    public Member signup(String email, String password, String nickname) {

        // 이메일 중복 체크
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("중복된 이메일입니다.");
        }

        // 비밀번호 조건 체크
        if (!password.matches(PASSWORD_REGEX)) {
            throw new RuntimeException("비밀번호 형식이 올바르지 않습니다. (소문자/숫자/특수문자 포함, 8~49자)");
        }

        // 이메일 도메인으로 관계자 여부 판단
        boolean managerFlag = email.endsWith("@chungbuk.ac.kr");
        Role role = managerFlag ? Role.BUSINESS : Role.USER;

        Member member = Member.builder()
                .email(email)
                .password(password)  // TODO: 나중에 BCrypt로 암호화
                .nickname(nickname)
                .role(role)
                .manager(managerFlag)
                .build();

        return memberRepository.save(member);
    }

    // 로그인
    public Member login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!member.getPassword().equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    // 회원 조회
    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    }

    //회원 탈퇴
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        memberRepository.delete(member);
    }

    public void toggleCleanbot(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // Member 엔티티에 만들어둔 메서드 호출
        member.toggleCleanbot();
        memberRepository.save(member);
    }
}


