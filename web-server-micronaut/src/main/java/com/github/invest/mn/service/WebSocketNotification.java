package com.github.invest.mn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import com.google.common.collect.ImmutableMap;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.ServerWebSocket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverStatus;

@Slf4j
@ServerWebSocket("/solution/{sessionId}")
public class WebSocketNotification implements NotificationService {

    private WebSocketBroadcaster broadcaster;
    private ObjectMapper mapper;

    public WebSocketNotification(WebSocketBroadcaster broadcaster, ObjectMapper mapper) {
        this.broadcaster = broadcaster;
        this.mapper = mapper;
    }

    @OnMessage
    public void onMessage(String sessionId, String message, WebSocketSession session) {

    }

    private void broadcast(String data) {
        broadcaster.broadcastSync(data);
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
