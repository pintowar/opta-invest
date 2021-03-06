package com.github.invest;

import com.github.invest.domain.InvestmentSolution;
import com.github.invest.service.NotificationService;
import com.github.invest.service.impl.SolverService;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class Application {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("InvestmentSolver-");
        executor.initialize();
        return executor;
    }

    @Bean
    public SolverService solverService(NotificationService notificationService,
                                       SolverManager<InvestmentSolution, Long> solverManager,
                                       @Qualifier("taskExecutor") ThreadPoolTaskExecutor executor) {
        return new SolverService(notificationService, solverManager, executor);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
