/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import com.github.invest.dto.AssetClassAllocationDTO;
import com.github.invest.dto.AssetClassDTO;
import com.github.invest.dto.InvestmentSolutionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@PlanningSolution
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class InvestmentSolution {

    @PlanningId
    private Long id;
    private String name;
    @ProblemFactProperty
    private InvestmentParametrization parametrization;
    @ProblemFactCollectionProperty
    private List<Region> regionList;
    @ProblemFactCollectionProperty
    private List<Sector> sectorList;
    @ProblemFactCollectionProperty
    private List<AssetClass> assetClassList;
    @PlanningEntityCollectionProperty
    private List<AssetClassAllocation> assetClassAllocationList;
    @PlanningScore
    private HardSoftLongScore score;

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider(id = "quantityMillisRange")
    public CountableValueRange<Long> getQuantityMillisRange() {
        return ValueRangeFactory.createLongValueRange(0L, InvestmentNumericUtil.MAXIMUM_QUANTITY_MILLIS + 1L);
    }

    /**
     * Not incremental.
     */
    public long calculateExpectedReturnMicros() {
        long expectedReturnMicros = 0L;
        for (AssetClassAllocation allocation : assetClassAllocationList) {
            expectedReturnMicros += allocation.getQuantifiedExpectedReturnMicros();
        }
        return expectedReturnMicros;
    }

    /**
     * Not incremental.
     */
    public long calculateStandardDeviationMicros() {
        long squaredFemtos = calculateStandardDeviationSquaredFemtos();
        return (long) Math.sqrt(squaredFemtos / 1000L);
    }

    /**
     * Not incremental.
     */
    public long calculateStandardDeviationSquaredFemtos() {
        long totalFemtos = 0L;
        for (AssetClassAllocation a : assetClassAllocationList) {
            for (AssetClassAllocation b : assetClassAllocationList) {
                if (a == b) {
                    totalFemtos += a.getQuantifiedStandardDeviationRiskMicros() * b.getQuantifiedStandardDeviationRiskMicros()
                            * 1000L;
                } else {
                    // Matches twice: once for (A, B) and once for (B, A)
                    long correlationMillis = a.getAssetClass().getCorrelationMillisMap().get(b.getAssetClass());
                    totalFemtos += a.getQuantifiedStandardDeviationRiskMicros() * b.getQuantifiedStandardDeviationRiskMicros()
                            * correlationMillis;
                }
            }
        }
        return totalFemtos;
    }

    public Map<Region, Long> calculateRegionQuantityMillisTotalMap() {
        Map<Region, Long> totalMap = new HashMap<>(regionList.size());
        for (Region region : regionList) {
            totalMap.put(region, 0L);
        }
        for (AssetClassAllocation allocation : assetClassAllocationList) {
            Long quantityMillis = allocation.getQuantityMillis();
            if (quantityMillis != null) {
                totalMap.put(allocation.getRegion(),
                        totalMap.get(allocation.getRegion()) + quantityMillis);
            }
        }
        return totalMap;
    }

    public Map<Sector, Long> calculateSectorQuantityMillisTotalMap() {
        Map<Sector, Long> totalMap = new HashMap<>(regionList.size());
        for (Sector sector : sectorList) {
            totalMap.put(sector, 0L);
        }
        for (AssetClassAllocation allocation : assetClassAllocationList) {
            Long quantityMillis = allocation.getQuantityMillis();
            if (quantityMillis != null) {
                totalMap.put(allocation.getSector(),
                        totalMap.get(allocation.getSector()) + quantityMillis);
            }
        }
        return totalMap;
    }

    public InvestmentSolutionDTO toDTO() {
        List<AssetClassDTO> assets = assetClassList.stream().map(AssetClass::toDTO).collect(Collectors.toList());
        List<AssetClassAllocationDTO> allocations = assetClassAllocationList.stream().map(AssetClassAllocation::toDTO)
                .collect(Collectors.toList());
        return InvestmentSolutionDTO.of(id, name, parametrization, regionList, sectorList, assets, allocations,
                calculateExpectedReturnMicros(), calculateStandardDeviationMicros());
    }

}
