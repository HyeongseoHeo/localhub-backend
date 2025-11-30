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
     * [로그인 처리 핵심 메서드]
     * 로그인 시 프론트엔드에서 보낸 '이메일'이 파라미터로 들어옵니다.
     * DB에서 이메일로 회원을 찾아 UserDetails(MemberDetails)로 포장해서 반환합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. 입력받은 email로 회원을 찾습니다.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 회원이 없습니다: " + email));

        // 2. 찾은 회원 정보를 MemberDetails에 담아 반환합니다.
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
            // "ROLE_" 접두사가 있어야 Security 설정에서 hasRole() 등을 쓸 때 편합니다.
            String authorityName = "ROLE_" + member.getRole().name();
            return Collections.singletonList(new SimpleGrantedAuthority(authorityName));
        }

        // 비밀번호 반환 (DB에 저장된 암호화된 비밀번호)
        @Override
        public String getPassword() {
            return member.getPassword();
        }

        // [중요] 사용자 식별자 반환
        // 여기서는 JWT 토큰 생성 시 Subject로 '회원 ID(PK)'를 쓰기 위해 ID를 문자열로 반환합니다.
        @Override
        public String getUsername() {
            return String.valueOf(member.getId());
        }

        // --- 아래는 계정 상태 설정 (기본적으로 모두 true로 설정하여 로그인 허용) ---

        // 계정 만료 여부 (true: 만료 안 됨)
        @Override
        public boolean isAccountNonExpired() { return true; }

        // 계정 잠금 여부 (true: 잠기지 않음)
        @Override
        public boolean isAccountNonLocked() { return true; }

        // 비밀번호 만료 여부 (true: 만료 안 됨)
        @Override
        public boolean isCredentialsNonExpired() { return true; }

        // 계정 활성화 여부 (true: 활성화 됨)
        @Override
        public boolean isEnabled() { return true; }

        // 필요 시 서비스 로직에서 원본 Member 객체를 꺼내 쓰기 위한 메서드
        public Member getMember() {
            return member;
        }
    }
}