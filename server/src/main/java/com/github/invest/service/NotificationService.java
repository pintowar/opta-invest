package com.github.invest.service;

import com.github.invest.domain.InvestmentSolution;
import org.optaplanner.core.api.solver.SolverStatus;

public interface NotificationService {
    void notifyInvestmentChange(SolverStatus status, InvestmentSolution newBestSolution);
}
