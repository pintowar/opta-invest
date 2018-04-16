package com.github.invest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class StompConnectEventListener implements ApplicationListener<SessionConnectEvent> {

    private ConcurrentHashMap<String, String> sessionWebSocket;

    public StompConnectEventListener(ConcurrentHashMap<String, String> sessionWebSocket) {
        this.sessionWebSocket = sessionWebSocket;
    }

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String httpSession = (String) sha.getSessionAttributes()
                .get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);
        String socketSession = sha.getSessionId();
        log.info("HttpSession ID: {}, Socket Session ID: {}", httpSession, socketSession);
        sessionWebSocket.put(httpSession, sha.getSessionId());
    }
}
