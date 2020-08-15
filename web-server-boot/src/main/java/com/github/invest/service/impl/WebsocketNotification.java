package com.github.invest.service.impl;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebsocketNotification implements NotificationService {

    private SimpMessageSendingOperations messagingTemplate;

    public WebsocketNotification(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notifyInvestmentChange(SolverStatus status, InvestmentSolution newBestSolution) {
        ImmutableMap data = ImmutableMap.of("id", newBestSolution.getId(),
                "portfolio", newBestSolution.toDTO(),
                "status", status);
        log.info("{}, {}, {}", newBestSolution.getId(), status, newBestSolution.getScore().toString());
        messagingTemplate.convertAndSend("/topic/solution", data);
    }

}
