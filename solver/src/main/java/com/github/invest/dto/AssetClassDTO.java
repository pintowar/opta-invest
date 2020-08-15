package com.github.invest.dto;

import com.github.invest.domain.AssetClass;
import com.github.invest.domain.Region;
import com.github.invest.domain.Sector;
import com.github.invest.domain.util.InvestmentNumericUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AssetClassDTO {

    private Long id;
    private String name;
    private Long regionId;
    private Long sectorId;
    private long expectedReturnMillis;
    private long standardDeviationRiskMillis;

    private Map<String, Long> correlationMillisMap;

    public String getExpectedReturnLabel() {
        return InvestmentNumericUtil.formatMillisAsPercentage(expectedReturnMillis);
    }

    public String getStandardDeviationRiskLabel() {
        return InvestmentNumericUtil.formatMillisAsPercentage(standardDeviationRiskMillis);
    }

    public AssetClass toAssetClass(Map<Long, Region> regionList, Map<Long, Sector> sectorList) {
        return new AssetClass(id, name, regionList.get(regionId), sectorList.get(sectorId), expectedReturnMillis,
                standardDeviationRiskMillis, null);
    }

    private Long idFromString(String key) {
        return Long.valueOf(key.split("-")[0]);
    }

    public Map<AssetClass, Long> correlate(Map<Long, AssetClass> assetClassList) {
        return correlationMillisMap.entrySet().stream().collect(Collectors.toMap(
                it -> assetClassList.get(this.idFromString(it.getKey())).clone(),
                Map.Entry::getValue
        ));
    }
}
