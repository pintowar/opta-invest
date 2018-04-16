package com.github.invest.dto;

import com.github.invest.domain.AssetClass;
import com.github.invest.domain.AssetClassAllocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetClassAllocationDTO {

    private Long id;
    private Long assetClassId;
    private Long quantityMillis;

    public AssetClassAllocation toAssetClassAllocation(Map<Long, AssetClass> idAssets) {
        return AssetClassAllocation.of(id, idAssets.get(assetClassId), quantityMillis);
    }
}
