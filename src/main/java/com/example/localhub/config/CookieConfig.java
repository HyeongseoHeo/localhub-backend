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

            // Render 백엔드 도메인을 정확하게 설정해야 한다
            context.setSessionCookieDomain("localhub-backend-ljtr.onrender.com");

            context.setSessionCookiePath("/");
        });
    }

}


