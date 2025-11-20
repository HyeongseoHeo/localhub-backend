package com.example.localhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 API 경로 허용 (더 안전)
                .allowedOrigins(
                        "http://localhost:5173",
                        "https://jere-trispermous-festively.ngrok-free.dev"
                )
                .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true);  // 세션 쿠키 허용!
    }
}
