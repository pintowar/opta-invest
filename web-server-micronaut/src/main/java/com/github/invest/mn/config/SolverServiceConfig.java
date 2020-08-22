package com.github.invest.mn.config;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import com.github.invest.service.impl.SolverService;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import javax.inject.Singleton;

@Slf4j
@Factory
public class SolverServiceConfig {

    public static final String DEFAULT_SOLVER_CONFIG_URL = "solverConfig.xml";

    @Singleton
    public SolverService names(NotificationService notificationService,
                               SolverManager<InvestmentSolution, Long> solverManager) {

        return new SolverService(notificationService, solverManager, null);
    }

    @Singleton
    public <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> solverManager(
            @Value("${optaplanner.parallel-solver-count:AUTO}") String parallelSolverCount,
            SolverFactory solverFactory) {
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();
        if (parallelSolverCount != null) {
            solverManagerConfig.setParallelSolverCount(parallelSolverCount);
        }
        return SolverManager.create(solverFactory, solverManagerConfig);
    }

    @Singleton
    public <Solution_> ScoreManager<Solution_> scoreManager(SolverFactory solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @Singleton
    public <Solution_> SolverFactory<Solution_> solverFactory(SolverConfig solverConfig) {
        return SolverFactory.create(solverConfig);
    }

    @Singleton
    public SolverConfig solverConfig(@Value("${optaplanner.solver-config-xml}") String solverConfigXml) {
        ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();
        log.debug("Solver Config: {}", solverConfigXml);

        SolverConfig solverConfig;
        if (solverConfigXml != null) {
            if (beanClassLoader.getResource(solverConfigXml) == null) {
                throw new IllegalStateException("Invalid optaplanner.solverConfigXml property (" + solverConfigXml
                        + "): that classpath resource does not exist.");
            }
            solverConfig = SolverConfig.createFromXmlResource(solverConfigXml, beanClassLoader);
        } else if (beanClassLoader.getResource(DEFAULT_SOLVER_CONFIG_URL) != null) {
            solverConfig = SolverConfig.createFromXmlResource(
                    DEFAULT_SOLVER_CONFIG_URL, beanClassLoader);
        } else {
            solverConfig = new SolverConfig(beanClassLoader);
        }

//        applySolverProperties(solverConfig);
        return solverConfig;
    }
}