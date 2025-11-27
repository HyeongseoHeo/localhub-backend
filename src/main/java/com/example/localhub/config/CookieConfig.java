package com.example.localhub.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> sessionCookieCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {

            // 세션 쿠키 도메인 Render로 강제 설정
            context.setSessionCookieDomain(".onrender.com");

            // 모든 경로에서 유효하게
            context.setSessionCookiePath("/");
        });
    }

}

