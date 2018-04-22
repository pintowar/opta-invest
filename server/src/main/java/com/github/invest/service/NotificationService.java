package com.github.invest.service;

import com.github.invest.domain.InvestmentSolution;

public interface NotificationService {
    void notifyInvestmentChange(InvestmentSolution newBestSolution);
}
