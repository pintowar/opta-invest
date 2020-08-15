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
import com.github.invest.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

@Slf4j
public class SolverService {

    private NotificationService notificationService;
    private Executor executor;
    private SolverManager<InvestmentSolution, Long> solverManager;
    private ConcurrentMap<Long, InvestmentSolution> investmentIdMap = new ConcurrentHashMap<>();

    public SolverService(NotificationService notificationService,
                         SolverManager<InvestmentSolution, Long> solverManager,
                         Executor executor) {
        this.notificationService = notificationService;
        this.solverManager = solverManager;
        this.executor = executor;
    }

    public void terminate(Long investmentId) {
        if (SolverStatus.SOLVING_ACTIVE.equals(solverManager.getSolverStatus(investmentId))) {
            InvestmentSolution solution = getInvestmentById(investmentId);
            if (solution != null) {
                solverManager.terminateEarly(investmentId);
                notificationService.notifyInvestmentChange(getStatusByInvestmentId(investmentId), solution);
            }
        } else {
            throw new IllegalStateException("The investment with id (" + investmentId
                    + ") is not being solved currently.");
        }
    }

    public Flux<ImmutablePair<Long, String>> solve(InvestmentSolution investment) {
        val flux = Flux.<ImmutablePair<Long, String>>create(emitter -> {
            solverManager.solve(investment.getId(), investment, (it) -> {
                log.info("New best solution found for investment id ({}) is {}.", investment.getId(),
                        investment.getScore());
                emitter.next(ImmutablePair.of(investment.getId(), investment.getScore().toString()));
            });
            emitter.complete();
        }, FluxSink.OverflowStrategy.LATEST).subscribeOn(Schedulers.fromExecutor(executor));

        return flux.sample(Duration.ofSeconds(1));
    }

    public void asyncSolve(InvestmentSolution investment) {
        final Long investmentId = investment.getId();
        solverManager.solveAndListen(
                investmentId,
                (it) -> it.equals(investment.getId()) ? investment : null,
                (it) -> {
                    investmentIdMap.put(it.getId(), it);
                    notificationService.notifyInvestmentChange(getStatusByInvestmentId(it.getId()), it);
                });
    }

    public InvestmentSolution getInvestmentById(Long investmentId) {
        return investmentIdMap.get(investmentId);
    }

    public SolverStatus getStatusByInvestmentId(Long investmentId) {
        return solverManager.getSolverStatus(investmentId);
    }

}
