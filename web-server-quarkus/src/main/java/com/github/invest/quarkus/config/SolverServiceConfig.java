package com.github.invest.quarkus.config;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import com.github.invest.service.impl.SolverService;
import org.optaplanner.core.api.solver.SolverManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class SolverServiceConfig {

    @Produces
    SolverService names(NotificationService notificationService,
                        SolverManager<InvestmentSolution, Long> solverManager) {

        return new SolverService(notificationService, solverManager, null);
    }
}