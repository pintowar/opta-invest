package com.github.invest.service.impl;

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.InvestmentRepository;
import com.github.invest.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;

@Slf4j
@Service
public class SolverService {

    private SolverFactory<InvestmentSolution> solverFactory = SolverFactory
            .createFromXmlResource("com/github/invest/investmentSolverConfig.xml");

    private InvestmentRepository investmentRepository;
    private NotificationService notificationService;
    private ThreadPoolTaskExecutor executor;

    public SolverService(InvestmentRepository investmentRepository, NotificationService notificationService,
                         @Qualifier("taskExecutor") ThreadPoolTaskExecutor executor) {
        this.investmentRepository = investmentRepository;
        this.notificationService = notificationService;
        this.executor = executor;
    }

    public void terminate(Long investmentId) {
        Solver<InvestmentSolution> solver = investmentRepository.getSolverByInvestmentId(investmentId);
        if (null != solver) {
            solver.terminateEarly();
        } else {
            throw new IllegalStateException("The investment with id (" + investmentId
                    + ") is not being solved currently.");
        }
    }

    public void terminateAndClean(Long investmentId) {
        terminate(investmentId);
        investmentRepository.terminateAndCleanByInvestmentId(investmentId);
    }

    public Flux<ImmutablePair<Long, String>> solve(InvestmentSolution investment) {
        val flux = Flux.<ImmutablePair<Long, String>>create(emitter -> {
            Solver<InvestmentSolution> solver = solverFactory.buildSolver();
            solver.addEventListener(event -> {
                if (event.isEveryProblemFactChangeProcessed()) {
                    log.info("New best solution found for investment id ({}) is {}.", investment.getId(),
                            event.getNewBestScore());
                    emitter.next(ImmutablePair.of(investment.getId(), event.getNewBestScore().toString()));
                }
            });
            solver.solve(investment);
            emitter.complete();
        }, FluxSink.OverflowStrategy.LATEST).subscribeOn(Schedulers.fromExecutor(executor));

        return flux.sample(Duration.ofSeconds(1));
    }

    @Async
    public void asyncSolve(InvestmentSolution investment) {
        Long investmentId = investment.getId();
        Solver<InvestmentSolution> solver = solverFactory.buildSolver();
        investmentRepository.associateSolverToInvestmentId(investmentId, solver);
        InvestmentSolution sol = null;
        solver.addEventListener(event -> {
            if (event.isEveryProblemFactChangeProcessed()) {
                notificationService.notifyInvestmentChange(event.getNewBestSolution());
            }
        });

        try {
            investmentRepository.solveStatusForInvestmentId(investmentId);
            sol = solver.solve(investment);
        } finally {
            investmentRepository.terminateAndCleanByInvestmentId(investmentId);
            notificationService.notifyInvestmentChange(sol);
        }
    }

}
