package com.github.invest.mn.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.domain.InvestmentSolution;
import com.github.invest.dto.InvestmentSolutionDTO;
import com.github.invest.dto.InvestmentStatusDTO;
import com.github.invest.service.impl.SolverService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.optaplanner.core.api.solver.SolverStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller("/api")
public class InvestController {

    private SolverService solverService;
    private ObjectMapper mapper;

    private String resource = "portfolios.json";
    private TypeReference<List<InvestmentSolutionDTO>> investmentRef = new TypeReference<>() {
    };

    public InvestController(SolverService solverService, ObjectMapper mapper) {
        this.solverService = solverService;
        this.mapper = mapper;
    }

    @SneakyThrows
    private InputStream portfoliosInputStream() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.getResourceAsStream(resource);
    }

    @Get(value = "/session-id", produces = MediaType.TEXT_PLAIN)
    public String sessionId(Session session) {
        return session.getId();
    }

    @Get(value = "/portfolios", produces = MediaType.APPLICATION_JSON)
    public List<InvestmentSolutionDTO> portfolios() throws IOException {
        return mapper.readValue(portfoliosInputStream(), investmentRef);
    }

    @Get(value = "/portfolio/{id}", produces = MediaType.APPLICATION_JSON)
    public Mono<InvestmentStatusDTO> portfolio(@PathVariable("id") Long id) throws IOException {
        SolverStatus status = solverService.getStatusByInvestmentId(id);
        InvestmentSolution invest = solverService.getInvestmentById(id);

        if (invest != null) {
            log.info("Found current solution if id {}", invest.getId());
            return Mono.just(InvestmentStatusDTO.of(status, invest.toDTO()));
        } else {
            List<InvestmentSolutionDTO> dtos = mapper.readValue(portfoliosInputStream(), investmentRef);
            Optional<InvestmentStatusDTO> resp = dtos.stream().filter(it -> id.equals(it.getId()))
                    .map(it -> InvestmentStatusDTO.of(status, it)).findFirst();
            return resp.map(Mono::just).orElseGet(Mono::empty);
        }
    }

    @Post(value = "/portfolio/{id}/allocate", consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public Flux<ImmutablePair<Long, String>> solve(@PathVariable("id") Long id,
                                                   @Body InvestmentSolutionDTO solutionDto) {
        if (id != null && id.equals(solutionDto.getId()))
            return solverService.solve(solutionDto.toInvestmentSolution());
        else return Flux.empty();
    }

    @Post(value = "/portfolio/{id}/async-allocate", consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public Mono<SolverStatus> asyncSolve(@PathVariable("id") Long id,
                                         @Body InvestmentSolutionDTO solutionDto) {
        if (id != null && id.equals(solutionDto.getId())) {
            try {
                solverService.asyncSolve(solutionDto.toInvestmentSolution());
            } catch (IllegalStateException e) {
                log.info(e.getMessage());
            }
            return Mono.just(solverService.getStatusByInvestmentId(solutionDto.getId()));
        } else return Mono.empty();
    }

    @Get(value = "/portfolio/{id}/terminate", produces = MediaType.APPLICATION_JSON)
    public Mono<Boolean> terminate(@PathVariable("id") Long id) {
        try {
            solverService.terminate(id);
            return Mono.just(true);
        } catch (IllegalStateException e) {
            return Mono.just(false);
        }
    }
}
