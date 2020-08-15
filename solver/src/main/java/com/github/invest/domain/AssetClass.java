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
import com.github.invest.dto.AssetClassDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(exclude = {"correlationMillisMap"})
public class AssetClass {

    @Id
    @PlanningId
    private Long id;
    private String name;

    @ManyToOne
    private Region region;

    @ManyToOne
    private Sector sector;

    private long expectedReturnMillis; // In millis (so multiplied by 1000)
    private long standardDeviationRiskMillis; // In millis (so multiplied by 1000)

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "assets_correlation",
            joinColumns = {@JoinColumn(name = "asset_class_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "other_asset_class_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "asset_class_id")
    private Map<AssetClass, Long> correlationMillisMap;

    @ManyToOne
    private InvestmentSolution investmentSolution;

    public AssetClass(Long id, String name, Region region, Sector sector, long expectedReturnMillis,
                      long standardDeviationRiskMillis, Map<AssetClass, Long> correlationMillisMap) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.sector = sector;
        this.expectedReturnMillis = expectedReturnMillis;
        this.standardDeviationRiskMillis = standardDeviationRiskMillis;
        this.correlationMillisMap = correlationMillisMap;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getCorrelationLabel(AssetClass other) {
        long correlationMillis = correlationMillisMap.get(other);
        return InvestmentNumericUtil.formatMillisAsNumber(correlationMillis);
    }

    @Override
    public String toString() {
        return id + "-" + name;
    }

    public AssetClass clone() {
        return new AssetClass(id, name, region, sector, expectedReturnMillis, standardDeviationRiskMillis,
                correlationMillisMap);
    }

    public AssetClassDTO toDTO() {
        Map<String, Long> corr = correlationMillisMap.entrySet().stream().collect(Collectors.toMap(
                it -> it.getKey().toString(), Map.Entry::getValue
        ));
        return AssetClassDTO.of(id, name, region.getId(), sector.getId(), expectedReturnMillis,
                standardDeviationRiskMillis, corr);
    }

}
