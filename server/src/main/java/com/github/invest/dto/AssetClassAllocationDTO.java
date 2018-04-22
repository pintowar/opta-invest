package com.github.invest.dto;

import com.github.invest.domain.AssetClass;
import com.github.invest.domain.AssetClassAllocation;
import com.github.invest.domain.util.InvestmentNumericUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AssetClassAllocationDTO {

    private Long id;
    private Long assetClassId;
    private Long quantityMillis;

    public String getQuantityLabel() {
        if (quantityMillis == null) {
            return "";
        }
        return InvestmentNumericUtil.formatMillisAsPercentage(quantityMillis);
    }

    public AssetClassAllocation toAssetClassAllocation(Map<Long, AssetClass> idAssets) {
        return AssetClassAllocation.of(id, idAssets.get(assetClassId), quantityMillis);
    }
}
