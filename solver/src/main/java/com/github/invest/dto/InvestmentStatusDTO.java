package com.github.invest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.solver.SolverStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class InvestmentStatusDTO {
    private SolverStatus status;
    private InvestmentSolutionDTO investment;
}
