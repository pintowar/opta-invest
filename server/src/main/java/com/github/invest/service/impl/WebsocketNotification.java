package com.github.invest.service.impl;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebsocketNotification implements NotificationService {

    private SimpMessageSendingOperations messagingTemplate;

    private ConcurrentHashMap<String, String> sessionWebSocket;

    public WebsocketNotification(SimpMessageSendingOperations messagingTemplate, ConcurrentHashMap<String, String> sessionWebSocket) {
        this.messagingTemplate = messagingTemplate;
        this.sessionWebSocket = sessionWebSocket;
    }

    @Override
    public void notifyInvestmentChange(String sessionId, InvestmentSolution newBestSolution) {
        log.info(newBestSolution.getScore().toString());
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        String wsSession = sessionWebSocket.get(sessionId);
        headerAccessor.setSessionId(wsSession);
        headerAccessor.setLeaveMutable(true);
        messagingTemplate.convertAndSendToUser(wsSession, "/queue/solution",
                newBestSolution.getAssetClassAllocationList(), headerAccessor.getMessageHeaders());
    }

}
