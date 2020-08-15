package com.github.invest.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invest.dto.InvestmentSolutionDTO;
import com.github.invest.repository.InvestmentSolutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class StartupConfig {

    private ObjectMapper mapper;
    private InvestmentSolutionRepository investmentSolutionRepository;
    private Resource resource = new ClassPathResource("./portfolios.json");
    private TypeReference<List<InvestmentSolutionDTO>> investmentRef = new TypeReference<>() {
    };

    public StartupConfig(ObjectMapper mapper, InvestmentSolutionRepository investmentSolutionRepository) {
        this.mapper = mapper;
        this.investmentSolutionRepository = investmentSolutionRepository;
    }

    @EventListener
    public void handleContextStart(ContextRefreshedEvent cse) throws IOException {
        List<InvestmentSolutionDTO> dtos = mapper.readValue(resource.getInputStream(), investmentRef);
        log.info("Found {} investment entities during startup.", dtos.size());
//        investmentSolutionRepository.saveAll(dtos);
//        log.info("Persisted all {} investments.", dtos.size());
    }
}
