package com.github.invest.service.impl;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.InvestmentRepository;
import com.github.invest.service.SolverStatus;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.Solver;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class LocalInvestmentRepo implements InvestmentRepository {

    private ConcurrentMap<Long, SolverStatus> investmentIdToSolverStateMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, Solver<InvestmentSolution>> investmentIdToSolverMap = new ConcurrentHashMap<>();


    @Override
    public Solver<InvestmentSolution> getSolverByInvestmentId(Long investmentId) {
        return investmentIdToSolverMap.get(investmentId);
    }

    @Override
    @Synchronized
    public InvestmentSolution getInvestmentById(Long investmentId) {
        if (investmentIdToSolverMap.containsKey(investmentId)) {
            return investmentIdToSolverMap.get(investmentId).getBestSolution();
        }
        return null;
    }

    @Override
    public SolverStatus getStatusByInvestmentId(Long investmentId) {
        return investmentIdToSolverStateMap.get(investmentId);
    }

    @Override
    public void scheduledStatusForInvestmentId(Long investmentId) {
        investmentIdToSolverStateMap.compute(investmentId, (k, solverStatus) -> {
            if (solverStatus != null && solverStatus != SolverStatus.TERMINATED) {
                throw new IllegalStateException("The investment with id (" + investmentId
                        + ") is already solving with solverStatus (" + solverStatus + ").");
            }
            return SolverStatus.SCHEDULED;
        });
        log.info("Scheduled solver for investmentId ({})...", investmentId);
    }

    @Override
    @Synchronized
    public void terminateAndCleanByInvestmentId(Long investmentId) {
        investmentIdToSolverMap.remove(investmentId);
        investmentIdToSolverStateMap.put(investmentId, SolverStatus.TERMINATED);
    }

    @Override
    public void solveStatusForInvestmentId(Long investmentId) {
        investmentIdToSolverStateMap.put(investmentId, SolverStatus.SOLVING);
    }

    @Override
    public void associateSolverToInvestmentId(Long investmentId, Solver<InvestmentSolution> solver) {
        investmentIdToSolverMap.put(investmentId, solver);
    }
}
