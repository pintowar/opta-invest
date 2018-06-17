<template>
  <div id="assets">
    <b-row>
      <h1>{{ title }}</h1>
    </b-row>
    <b-row>
      <b-col cols="6">
        <b-table striped hover :items="regions" :fields="fields">
          <template slot="quantity" slot-scope="row">
            <b-input-group size="sm" append="%">
              <b-form-input id="region-std" v-model.number="row.item.quantity" type="number" @change="regionChange(row.index)" :disabled="solving"/>
            </b-input-group>
          </template>
        </b-table>
      </b-col>
      <b-col cols="6">
        <b-table striped hover :items="sectors" :fields="fields">
          <template slot="quantity" slot-scope="row">
            <b-input-group size="sm" append="%">
              <b-form-input id="sector-std" v-model.trim="row.item.quantity" type="number" @change="sectorChange(row.index)" :disabled="solving"/>
            </b-input-group>
          </template>
        </b-table>
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
  props: ['title', 'portfolio', 'status'],
  data () {
    return {
      fields: ['id', 'name', 'quantity']
    }
  },
  computed: {
    solving () {
      return this.status === 'SOLVING'
    },
    regions () {
      return (this.portfolio ? this.portfolio.regionList || [] : []).map(e => { e.quantity = e.quantityMillisMaximum / 10; return e })
    },
    sectors () {
      return (this.portfolio ? this.portfolio.sectorList || [] : []).map(e => { e.quantity = e.quantityMillisMaximum / 10; return e })
    },
    assets () {
      const regionsId = this.regions.reduce((acc, e) => { acc[e.id] = e; return acc }, {})
      const sectorsId = this.sectors.reduce((acc, e) => { acc[e.id] = e; return acc }, {})

      const assetsList = this.portfolio ? this.portfolio.assetClassList || [] : []
      return assetsList.map(e => ({
        id: e.id,
        name: e.name,
        region: regionsId[e.regionId].name,
        sector: sectorsId[e.sectorId].name,
        expectedReturnLabel: e.expectedReturnLabel,
        standardDeviationRiskLabel: e.standardDeviationRiskLabel}))
    }
  },
  methods: {
    regionChange (idx) {
      const aux = parseFloat(this.regions[idx].quantity)
      const qty = Math.max(Math.min(isNaN(aux) ? 0.0 : aux, 100.0), 0.0)
      this.portfolio.regionList[idx].quantityMillisMaximum = qty * 10
      this.portfolio.regionList[idx].quantityMaximumLabel = `${qty}%`
    },
    sectorChange (idx) {
      const aux = parseFloat(this.sectors[idx].quantity)
      const qty = Math.max(Math.min(isNaN(aux) ? 0.0 : aux, 100.0), 0.0)
      this.portfolio.sectorList[idx].quantityMillisMaximum = qty * 10
      this.portfolio.sectorList[idx].quantityMaximumLabel = `${qty}%`
    }
  }
}
</script>