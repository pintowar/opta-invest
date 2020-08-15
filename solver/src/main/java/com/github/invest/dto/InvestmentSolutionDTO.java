package com.github.invest.dto;

import com.github.invest.domain.*;
import com.github.invest.domain.util.InvestmentNumericUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class InvestmentSolutionDTO {
    private Long id;
    private String name;
    private InvestmentParametrization parametrization;
    private List<Region> regionList;
    private List<Sector> sectorList;
    private List<AssetClassDTO> assetClassList;
    private List<AssetClassAllocationDTO> assetClassAllocationList;

    private long expectedReturnMicros;
    private long standardDeviationMicros;

    public String getExpectedReturnLabel() {
        return InvestmentNumericUtil.formatMicrosAsPercentage(expectedReturnMicros);
    }

    public String getStandardDeviationLabel() {
        return InvestmentNumericUtil.formatMicrosAsPercentage(standardDeviationMicros);
    }

    public InvestmentSolution toInvestmentSolution() {
        val idRegions = regionList.stream().collect(toMap(Region::getId, it -> it));
        val idSectors = sectorList.stream().collect(toMap(Sector::getId, it -> it));

        val assets = assetClassList.stream().map(it -> it.toAssetClass(idRegions, idSectors)).collect(toList());
        val idAssets = assets.stream().collect(toMap(AssetClass::getId, it -> it));

        for (int i = 0; i < assets.size(); i++) {
            assets.get(i).setCorrelationMillisMap(assetClassList.get(i).correlate(idAssets));
        }

        val assetsAllocation = assetClassAllocationList.stream().map(it -> it.toAssetClassAllocation(idAssets))
                .collect(toList());

        return InvestmentSolution.of(id, name, parametrization, regionList, sectorList, assets, assetsAllocation, null);
    }
}
