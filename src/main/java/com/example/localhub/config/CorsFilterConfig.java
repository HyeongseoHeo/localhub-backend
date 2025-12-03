package com.example.localhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsFilterConfig {

    // 아까 WebConfig에 있던 CORS 규칙을 그대로 가져옵니다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // 허용할 출처 (Allowed Origins)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "https://jere-trispermous-festively.ngrok-free.dev",
                "https://opensource09.vercel.app"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        config.setExposedHeaders(Arrays.asList("Authorization")); // JWT 토큰 노출을 위해 추가 권장

        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용
        return source;
    }
}