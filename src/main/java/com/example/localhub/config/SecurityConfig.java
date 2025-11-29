package com.example.localhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // BCryptPasswordEncoder를 빈으로 등록하여 서비스에서 주입받아 사용합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO: JWT 인증을 위해 WebSecurityConfigurerAdapter (또는 SecurityFilterChain) 설정이 필요함
}
