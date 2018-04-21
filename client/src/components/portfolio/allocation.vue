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
        <b-table striped hover :items="allocations"></b-table>
      </b-col>
      <b-col cols="6">
      </b-col>
    </b-row>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'allocation',
  props: ['title', 'portfolio', 'rtAllocations'],
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

    },
    coiso (data) {
      console.log(data)
    }
  }
}
</script>