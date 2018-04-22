package com.github.invest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.domain.InvestmentSolution;
import com.github.invest.dto.InvestmentSolutionDTO;
import com.github.invest.dto.InvestmentStatusDTO;
import com.github.invest.service.InvestmentRepository;
import com.github.invest.service.SolverStatus;
import com.github.invest.service.impl.SolverService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class InvestController {

    private SolverService solverService;
    private InvestmentRepository investmentRepository;
    private ObjectMapper mapper;

    public InvestController(SolverService solverService, InvestmentRepository investmentRepository,
                            ObjectMapper mapper) {
        this.solverService = solverService;
        this.investmentRepository = investmentRepository;
        this.mapper = mapper;
    }

    @GetMapping(value = "/api/portfolios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InvestmentSolutionDTO>> portfolios() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("./portfolios.json");
        List<InvestmentSolutionDTO> dtos = mapper.readValue(in, new TypeReference<List<InvestmentSolutionDTO>>() {
        });
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/api/portfolio/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<InvestmentStatusDTO> portfolio(@PathVariable("id") Long id) throws IOException {
        SolverStatus status = investmentRepository.getStatusByInvestmentId(id);
        InvestmentSolution invest = investmentRepository.getInvestmentById(id);
        if (invest != null) {
            log.info("Found current solution if id {}", invest.getId());
            return Mono.just(InvestmentStatusDTO.of(status, invest.toDTO()));
        } else {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("./portfolios.json");
            List<InvestmentSolutionDTO> dtos = mapper.readValue(in, new TypeReference<List<InvestmentSolutionDTO>>() {
            });
            Optional<InvestmentStatusDTO> resp = dtos.stream().filter(it -> id.equals(it.getId()))
                    .map(it -> InvestmentStatusDTO.of(status, it)).findFirst();
            return resp.map(Mono::just).orElseGet(Mono::empty);
        }
    }

    @PostMapping(value = "/api/portfolio/{id}/allocate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ImmutablePair<Long, String>> solve(@PathVariable("id") Long id,
                                                   @RequestBody InvestmentSolutionDTO solutionDto) {
        if (id != null && id.equals(solutionDto.getId()))
            return solverService.solve(solutionDto.toInvestmentSolution());
        else return Flux.empty();
    }

    @PostMapping(value = "/api/portfolio/{id}/async-allocate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolverStatus> asyncSolve(@PathVariable("id") Long id,
                                                   @RequestBody InvestmentSolutionDTO solutionDto) {
        if (id != null && id.equals(solutionDto.getId())) {
            try {
                investmentRepository.scheduledStatusForInvestmentId(id);
                solverService.asyncSolve(solutionDto.toInvestmentSolution());
            } catch (IllegalStateException e) {
                log.info(e.getMessage());
            }
            return ResponseEntity.ok(investmentRepository.getStatusByInvestmentId(solutionDto.getId()));
        } else return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/api/portfolio/{id}/terminate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> terminate(@PathVariable("id") Long id) {
        try {
            solverService.terminate(id);
            return ResponseEntity.ok(true);
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(false);
        }
    }

}
