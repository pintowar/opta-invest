package com.github.invest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.domain.InvestmentSolution;
import com.github.invest.dto.InvestmentSolutionDTO;
import com.github.invest.dto.InvestmentStatusDTO;
import com.github.invest.service.impl.SolverService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class InvestController {

    private SolverService solverService;
    private ObjectMapper mapper;

    private Resource resource = new ClassPathResource("./portfolios.json");
    private TypeReference<List<InvestmentSolutionDTO>> investmentRef = new TypeReference<>() {
    };


    public InvestController(SolverService solverService, ObjectMapper mapper) {
        this.solverService = solverService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/api/portfolios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InvestmentSolutionDTO>> portfolios() throws IOException {
        List<InvestmentSolutionDTO> dtos = mapper.readValue(resource.getInputStream(), investmentRef);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/api/portfolio/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<InvestmentStatusDTO> portfolio(@PathVariable("id") Long id) throws IOException {
        SolverStatus status = solverService.getStatusByInvestmentId(id);
        InvestmentSolution invest = solverService.getInvestmentById(id);

        if (invest != null) {
            log.info("Found current solution if id {}", invest.getId());
            return Mono.just(InvestmentStatusDTO.of(status, invest.toDTO()));
        } else {
            List<InvestmentSolutionDTO> dtos = mapper.readValue(resource.getInputStream(), investmentRef);
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
                solverService.asyncSolve(solutionDto.toInvestmentSolution());
            } catch (IllegalStateException e) {
                log.info(e.getMessage());
            }
            return ResponseEntity.ok(solverService.getStatusByInvestmentId(solutionDto.getId()));
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
