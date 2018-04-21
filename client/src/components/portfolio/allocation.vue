<template>
  <div id="assets">
    <b-row>
      <h1>{{ title }}</h1>
    </b-row>
    <b-row>
      <b-form>
        <b-button size="sm" variant="success" @click="allocateAssets">Allocate Assets</b-button>
        <b-button size="sm" variant="warning" @click="terminateAllocation">Terminate Allocation</b-button>
        <b-button size="sm" variant="danger" @click="destroySolution">Destroy</b-button>
      </b-form>
    </b-row>
    <b-row>
      <p>Maximum Standard Deviation: {{ parametrization }}</p>
    </b-row>
    <b-row>
      <b-col cols="6">
        <b-table striped hover :items="allocations" :fields="tableFields"></b-table>
      </b-col>
      <b-col cols="6">
          <allocation-chart :chart-data="chartAllocations"/>
      </b-col>
    </b-row>
  </div>
</template>

<script>
import Rainbow from 'color-rainbow'
import axios from 'axios'
import AllocationChart from '@/components/portfolio/chart.vue'

export default {
  name: 'allocation',
  props: ['title', 'portfolio', 'rtAllocations'],
  components: {
    'allocation-chart': AllocationChart
  },
  data () {
    return {
      tableFields: ['asset', 'quantityLabel']
    }
  },
  computed: {
    parametrization () {
      return ((this.portfolio || {}).parametrization || {}).standardDeviationMillisMaximum
    },
    allocations () {
      const assetsList = this.portfolio ? this.portfolio.assetClassList : []
      const assetsId = (assetsList || []).reduce((acc, e) => { acc[e.id] = e; return acc }, {})

      const initialAssets = (this.portfolio.assetClassAllocationList || []).map(e => ({
        asset: assetsId[e.assetClassId].name,
        quantityMillis: e.quantityMillis ? e.quantityMillis : 0,
        quantityLabel: e.quantityLabel ? e.quantityLabel : '0.0%'}))

      return this.rtAllocations.length === 0 ? initialAssets : this.rtAllocations
    },
    chartAllocations () {
      const colors = Rainbow.create(this.allocations.length).map(c => `rgb(${c.values.rgb.join(', ')})`)
      return {
        labels: this.allocations.map(e => e.asset),
        datasets: [{
          backgroundColor: colors,
          data: this.allocations.map(e => e.quantityMillis)
        }]
      }
    }
  },
  methods: {
    allocateAssets (evt) {
      const url = `/api/portfolio/${this.portfolio.id}/async-allocate`
      axios.post(url, this.portfolio)
      .then(res => {
        console.log(res.data)
      }, error => {
        console.error(error)
      })
    },
    terminateAllocation (evt) {

    },
    destroySolution (evt) {

    }
  }
}
</script>