package com.github.invest.config;

import com.github.invest.service.impl.WebSocketNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(solutionNotificationHandler(), "/solution/*")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketNotification solutionNotificationHandler() {
        return new WebSocketNotification();
    }
}