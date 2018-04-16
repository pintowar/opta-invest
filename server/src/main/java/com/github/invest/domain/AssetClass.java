/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.invest.domain;

import com.github.invest.domain.util.InvestmentNumericUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(exclude = {"correlationMillisMap"})
public class AssetClass {

    @PlanningId
    private Long id;
    private String name;
    private Region region;
    private Sector sector;
    private long expectedReturnMillis; // In millis (so multiplied by 1000)
    private long standardDeviationRiskMillis; // In millis (so multiplied by 1000)

    private Map<AssetClass, Long> correlationMillisMap;

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getExpectedReturnLabel() {
        return InvestmentNumericUtil.formatMillisAsPercentage(expectedReturnMillis);
    }

    public String getStandardDeviationRiskLabel() {
        return InvestmentNumericUtil.formatMillisAsPercentage(standardDeviationRiskMillis);
    }

    public String getCorrelationLabel(AssetClass other) {
        long correlationMillis = correlationMillisMap.get(other);
        return InvestmentNumericUtil.formatMillisAsNumber(correlationMillis);
    }

    @Override
    public String toString() {
        return id + "-" + name;
    }

    public AssetClass clone() {
        return AssetClass.of(id, name, region, sector, expectedReturnMillis, standardDeviationRiskMillis,
                correlationMillisMap);
    }

}
