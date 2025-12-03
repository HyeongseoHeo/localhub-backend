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

            context.setSessionCookieDomain("localhub-backend-ljtr.onrender.com");

            context.setSessionCookiePath("/");
        });
    }

}


