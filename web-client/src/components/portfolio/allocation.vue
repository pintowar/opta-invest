<template>
  <div id="assets">
    <b-row>
      <h1>{{ title }}</h1>
    </b-row>
    <b-row>
      <b-form>
        <b-button size="sm" variant="success" @click="allocateAssets">Allocate Assets</b-button>
        <b-button size="sm" variant="danger" @click="terminateAllocation">Terminate Allocation</b-button>
        <b-badge pill variant="primary">{{ status }}</b-badge>
      </b-form>
    </b-row>
    <b-row>
      <b-col cols="6">
        <b-container fluid>
          <b-row>
            <b-col sm="6">
              <label for="input-std">Maximum Standard Deviation:</label>
            </b-col>
            <b-col sm="3">
              <b-input-group size="sm" append="%">
                <b-form-input id="input-std" v-model.trim="parametrization" @change="parametrizationChange" type="number" :disabled="solving"/>
              </b-input-group>
            </b-col>
          </b-row>
        </b-container>
        <b-table striped hover :items="allocations" :fields="tableFields"/>
        <p>
          Expected Return: <b>{{ (portfolio || {}).expectedReturnLabel }}</b>,
          Standard Deviation Risk: <b>{{ (portfolio || {}).standardDeviationLabel }}</b>
        </p>
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
  props: ['title', 'portfolio', 'status'],
  components: {
    'allocation-chart': AllocationChart
  },
  data () {
    return {
      tableFields: ['asset', 'expectedReturn', 'standardDeviation', 'quantityLabel']
    }
  },
  computed: {
    solving () {
      return this.status === 'SOLVING_ACTIVE'
    },
    parametrization: {
      set (newVal) {
        this.portfolio.parametrization.standardDeviationMaximumLabel = `${newVal}%`
        this.portfolio.parametrization.standardDeviationMillisMaximum = newVal * 10
      },
      get () {
        return (((this.portfolio || {}).parametrization || {}).standardDeviationMillisMaximum || 0) / 10
      }
    },
    allocations () {
      const assetsList = this.portfolio ? this.portfolio.assetClassList : []
      const assetsId = (assetsList || []).reduce((acc, e) => { acc[e.id] = e; return acc }, {})

      return (this.portfolio.assetClassAllocationList || []).map(e => ({
        asset: assetsId[e.assetClassId].name,
        expectedReturn: assetsId[e.assetClassId].expectedReturnLabel,
        standardDeviation: assetsId[e.assetClassId].standardDeviationRiskLabel,
        quantityMillis: e.quantityMillis,
        quantityLabel: e.quantityLabel}))
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
    parametrizationChange () {
      const aux = parseFloat(this.parametrization)
      const qty = Math.max(Math.min(isNaN(aux) ? 0.0 : aux, 100.0), 0.0)
      this.parametrization = qty
    },
    allocateAssets () {
      const url = `/api/portfolio/${this.portfolio.id}/async-allocate`
      axios.post(url, this.portfolio)
      .then(res => {
        this.$emit('newStatus', res.data)
        console.log(res.data)
      }, error => {
        console.error(error)
      })
    },
    terminateAllocation () {
      const url = `/api/portfolio/${this.portfolio.id}/terminate`
      axios.get(url)
      .then(res => {
        console.log(res.data)
      }, error => {
        console.error(error)
      })
    }
  }
}
</script>
