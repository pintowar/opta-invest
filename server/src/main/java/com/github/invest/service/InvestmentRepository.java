package com.github.invest.service;

import com.github.invest.domain.InvestmentSolution;
import org.optaplanner.core.api.solver.Solver;

public interface InvestmentRepository {

    Solver<InvestmentSolution> getSolverByInvestmentId(Long investmentId);

    InvestmentSolution getInvestmentById(Long investmentId);

    SolverStatus getStatusByInvestmentId(Long investmentId);

    void scheduledStatusForInvestmentId(Long investmentId);

    void terminateAndCleanByInvestmentId(Long investmentId);

    void solveStatusForInvestmentId(Long investmentId);

    void associateSolverToInvestmentId(Long investmentId, Solver<InvestmentSolution> solver);
}
