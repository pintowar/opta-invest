package com.github.invest.quarkus.service;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverStatus;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class DummyNotification implements NotificationService {

    @Override
    public void notifyInvestmentChange(SolverStatus status, InvestmentSolution newBestSolution) {
        log.info("{}, {}, {}", newBestSolution.getId(), status, newBestSolution.getScore().toString());
    }
}
