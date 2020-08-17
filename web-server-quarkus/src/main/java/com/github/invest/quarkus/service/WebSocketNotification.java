package com.github.invest.quarkus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/solution/{sessionId}")
@ApplicationScoped
public class WebSocketNotification implements NotificationService {

    private Map<String, Session> sessions = new ConcurrentHashMap<>();
    private ObjectMapper mapper;

    public WebSocketNotification(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String username) {
        sessions.put(username, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("sessionId") String username) {
        sessions.remove(username);
    }

    @OnError
    public void onError(Session session, @PathParam("sessionId") String username, Throwable throwable) {
        sessions.remove(username);
    }

    private void broadcast(String data) {
        sessions.values().forEach(s ->
                s.getAsyncRemote().sendText(data, result -> {
                    if (result.getException() != null) {
                        log.error("Unable to send message: " + result.getException().getMessage(),
                                result.getException());
                    }
                })
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
