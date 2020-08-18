package com.github.invest.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketNotification extends TextWebSocketHandler implements NotificationService {

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = sessionIdFromSession(session);
        sessions.put(sessionId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = sessionIdFromSession(session);
        sessions.remove(sessionId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = sessionIdFromSession(session);
        sessions.remove(sessionId);
    }

    private String sessionIdFromSession(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private void broadcast(String data) {
        sessions.values().forEach(socket -> {
                    try {
                        socket.sendMessage(new TextMessage(data));
                    } catch (IOException e) {
                        log.error("Could not send message message through web socket!", e);
                    }
                }
        );
    }

    @Override
    @SneakyThrows
    public void notifyInvestmentChange(SolverStatus status, InvestmentSolution newBestSolution) {
        ImmutableMap data = ImmutableMap.of("id", newBestSolution.getId(),
                "portfolio", newBestSolution.toDTO(),
                "status", status);

        log.info("{}, {}, {}", newBestSolution.getId(), status, newBestSolution.getScore().toString());
        broadcast(mapper.writeValueAsString(data));
    }

}
