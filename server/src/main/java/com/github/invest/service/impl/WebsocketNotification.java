package com.github.invest.service.impl;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.InvestmentRepository;
import com.github.invest.service.NotificationService;
import com.github.invest.service.SolverStatus;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebsocketNotification implements NotificationService {

    private SimpMessageSendingOperations messagingTemplate;
    private InvestmentRepository investmentRepository;

    public WebsocketNotification(SimpMessageSendingOperations messagingTemplate,
                                 InvestmentRepository investmentRepository) {
        this.messagingTemplate = messagingTemplate;
        this.investmentRepository = investmentRepository;
    }

    @Override
    public void notifyInvestmentChange(InvestmentSolution newBestSolution) {
        SolverStatus status = investmentRepository.getStatusByInvestmentId(newBestSolution.getId());
        ImmutableMap data = ImmutableMap.of("id", newBestSolution.getId(),
                "portfolio", newBestSolution.toDTO(),
                "status", status);
        log.info("{}, {}, {}", newBestSolution.getId(), status, newBestSolution.getScore().toString());
        messagingTemplate.convertAndSend("/topic/solution", data);
    }

}
