package com.example.localhub.security.details;

import com.example.localhub.domain.member.Member;
import com.example.localhub.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * [핵심] 사용자 ID(여기서는 이메일)로 DB에서 회원 정보를 로드하고 Spring Security 객체로 변환
     * @param username JWT 토큰에 담긴 사용자 정보 (여기서는 MemberId나 Email을 사용해야 함)
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자가 없을 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // NOTE: username은 JWT 토큰의 Subject에서 꺼낸 사용자 ID(String)입니다.
        // 우리는 Member ID(Long)를 Subject로 사용할 것이므로, String으로 받은 ID를 Long으로 변환합니다.
        Long memberId;
        try {
            memberId = Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid Member ID format");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found with ID: " + username));

        return new MemberDetails(member);
    }

    /**
     * Spring Security가 사용하는 사용자 정보를 담는 내부 클래스
     */
    public static class MemberDetails implements UserDetails {

        private final Member member;

        public MemberDetails(Member member) {
            this.member = member;
        }

        // 사용자의 권한(Role)을 반환
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Member 엔티티의 Role(USER, BUSINESS 등)을 Spring Security 권한 객체로 변환
            String authorityName = "ROLE_" + member.getRole().name();
            return Collections.singletonList(new SimpleGrantedAuthority(authorityName));
        }

        // 비밀번호 반환 (BCrypt로 암호화된 비밀번호)
        @Override
        public String getPassword() {
            return member.getPassword();
        }

        // 사용자 이름 반환 (여기서는 Member ID를 String으로 반환)
        // JWT의 Subject(토큰의 주체)로 사용됨
        @Override
        public String getUsername() {
            return String.valueOf(member.getId());
        }

        // 계정 만료 여부 (기본 true)
        @Override
        public boolean isAccountNonExpired() { return true; }

        // 계정 잠금 여부 (기본 true)
        @Override
        public boolean isAccountNonLocked() { return true; }

        // 자격 증명 만료 여부 (기본 true)
        @Override
        public boolean isCredentialsNonExpired() { return true; }

        // 계정 활성화 여부 (기본 true)
        @Override
        public boolean isEnabled() { return true; }

        // 추가 편의 메서드: 실제 Member 객체를 반환 (필요할 때 사용)
        public Member getMember() {
            return member;
        }
    }
}