<template>
  <div id="assets">
    <b-row>
      <h1>{{ title }}</h1>
    </b-row>
    <b-row>
      <b-col cols="6">
        <b-table striped hover :items="regions"></b-table>
      </b-col>
      <b-col cols="6">
        <b-table striped hover :items="sectors"></b-table>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <b-table striped hover :items="assets"></b-table>
      </b-col>
    </b-row>
  </div>
</template>

<script>
export default {
  name: 'assets',
  props: ['title', 'portfolio'],
  computed: {
    regions () {
      return this.portfolio ? this.portfolio.regionList : []
    },
    sectors () {
      return this.portfolio ? this.portfolio.sectorList : []
    },
    assets () {
      const regionsId = (this.regions || []).reduce((acc, e) => { acc[e.id] = e; return acc }, {})
      const sectorsId = (this.sectors || []).reduce((acc, e) => { acc[e.id] = e; return acc }, {})

      const assetsList = this.portfolio ? this.portfolio.assetClassList : []
      return (assetsList || []).map(e => ({
        id: e.id,
        name: e.name,
        region: regionsId[e.regionId].name,
        sector: sectorsId[e.sectorId].name,
        expectedReturnMillis: e.expectedReturnMillis,
        standardDeviationRiskMillis: e.standardDeviationRiskMillis}))
    }
  }
}
</script>