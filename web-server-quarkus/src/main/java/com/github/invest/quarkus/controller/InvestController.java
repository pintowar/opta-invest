package com.github.invest.quarkus.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.domain.InvestmentSolution;
import com.github.invest.dto.InvestmentSolutionDTO;
import com.github.invest.dto.InvestmentStatusDTO;
import com.github.invest.service.impl.SolverService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.multi.MultiReactorConverters;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.optaplanner.core.api.solver.SolverStatus;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Path("/api")
public class InvestController {

    private SolverService solverService;
    private ObjectMapper mapper;

    private String resource = "portfolios.json";
    private TypeReference<List<InvestmentSolutionDTO>> investmentRef = new TypeReference<>() {
    };

    @Context
    private HttpServletRequest httpRequest;

    public InvestController(SolverService solverService, ObjectMapper mapper) {
        this.solverService = solverService;
        this.mapper = mapper;
    }

    @SneakyThrows
    private InputStream portfoliosInputStream() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl.getResourceAsStream(resource);
    }

    @GET
    @Path("/session-id")
    @Produces(MediaType.TEXT_PLAIN)
    public String sessionId() {
        return httpRequest.getSession().getId();
    }

    @GET
    @Path("/portfolios")
    @Produces(MediaType.APPLICATION_JSON)
    public List<InvestmentSolutionDTO> portfolios() throws IOException {
        return mapper.readValue(portfoliosInputStream(), investmentRef);
    }

    @GET
    @Path("/portfolio/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<InvestmentStatusDTO> portfolio(@PathParam("id") Long id) throws IOException {
        SolverStatus status = solverService.getStatusByInvestmentId(id);
        InvestmentSolution invest = solverService.getInvestmentById(id);

        if (invest != null) {
            log.info("Found current solution if id {}", invest.getId());
            return Uni.createFrom().item(InvestmentStatusDTO.of(status, invest.toDTO()));
        } else {
            List<InvestmentSolutionDTO> dtos = mapper.readValue(portfoliosInputStream(), investmentRef);
            Optional<InvestmentStatusDTO> resp = dtos.stream().filter(it -> id.equals(it.getId()))
                    .map(it -> InvestmentStatusDTO.of(status, it)).findFirst();
            return resp.map(Uni.createFrom()::item).orElseGet(Uni.createFrom()::nothing);
        }
    }

    @POST
    @Path("/portfolio/{id}/allocate")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<ImmutablePair<Long, String>> solve(@PathParam("id") Long id,
                                                    InvestmentSolutionDTO solutionDto) {
        if (id != null && id.equals(solutionDto.getId()))
            return Multi.createFrom().converter(MultiReactorConverters.fromFlux(),
                    solverService.solve(solutionDto.toInvestmentSolution()));
        else return Multi.createFrom().empty();
    }

    @POST
    @Path("/portfolio/{id}/async-allocate")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<SolverStatus> asyncSolve(@PathParam("id") Long id,
                                        InvestmentSolutionDTO solutionDto) {
        if (id != null && id.equals(solutionDto.getId())) {
            try {
                solverService.asyncSolve(solutionDto.toInvestmentSolution());
            } catch (IllegalStateException e) {
                log.info(e.getMessage());
            }
            return Uni.createFrom().item(solverService.getStatusByInvestmentId(solutionDto.getId()));
        } else return Uni.createFrom().nothing();
    }

    @GET
    @Path("/portfolio/{id}/terminate")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Boolean> terminate(@PathParam("id") Long id) {
        try {
            solverService.terminate(id);
            return Uni.createFrom().item(true);
        } catch (IllegalStateException e) {
            return Uni.createFrom().item(false);
        }
    }
}