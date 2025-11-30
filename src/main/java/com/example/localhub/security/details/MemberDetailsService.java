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

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Member member;

        // [핵심] 입력값에 '@'가 있으면 이메일로 찾고, 없으면 ID(숫자)로 찾기!
        if (input.contains("@")) {
            // 1. 로그인 할 때는 이메일이 들어옵니다.
            member = memberRepository.findByEmail(input)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 회원이 없습니다: " + input));
        } else {
            // 2. 토큰 검증 할 때는 ID(숫자)가 들어옵니다.
            try {
                Long id = Long.parseLong(input);
                member = memberRepository.findById(id)
                        .orElseThrow(() -> new UsernameNotFoundException("해당 ID를 가진 회원이 없습니다: " + input));
            } catch (NumberFormatException e) {
                throw new UsernameNotFoundException("잘못된 요청입니다: " + input);
            }
        }

        return new MemberDetails(member);
    }

    // --- 아래 MemberDetails 클래스는 그대로 두셔도 됩니다 ---
    public static class MemberDetails implements UserDetails {
        private final Member member;

        public MemberDetails(Member member) { this.member = member; }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            String authorityName = "ROLE_" + member.getRole().name();
            return Collections.singletonList(new SimpleGrantedAuthority(authorityName));
        }

        @Override
        public String getPassword() { return member.getPassword(); }

        @Override
        public String getUsername() { return String.valueOf(member.getId()); } // ID 반환

        @Override
        public boolean isAccountNonExpired() { return true; }
        @Override
        public boolean isAccountNonLocked() { return true; }
        @Override
        public boolean isCredentialsNonExpired() { return true; }
        @Override
        public boolean isEnabled() { return true; }

        // 서비스에서 써야 하므로 getter 추가
        public Member getMember() { return member; }
    }
}