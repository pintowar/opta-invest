package com.github.invest.dto;

import com.github.invest.service.SolverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class InvestmentStatusDTO {
    private SolverStatus status;
    private InvestmentSolutionDTO investment;
}
