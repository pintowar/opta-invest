package com.github.invest.dto;

import com.github.invest.domain.AssetClass;
import com.github.invest.domain.Region;
import com.github.invest.domain.Sector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetClassDTO {

    private Long id;
    private String name;
    private Long regionId;
    private Long sectorId;
    private long expectedReturnMillis;
    private long standardDeviationRiskMillis;

    private Map<String, Long> correlationMillisMap;

    public AssetClass toAssetClass(Map<Long, Region> regionList, Map<Long, Sector> sectorList) {
        return AssetClass.of(id, name, regionList.get(regionId), sectorList.get(sectorId), expectedReturnMillis,
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
