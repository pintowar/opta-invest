<template>
  <b-card no-body>
    <b-tabs card>
      <b-tab title="Allocation" active>
        <b-row>
          <h1>{{ title }}</h1>
        </b-row>
        <b-row>
          <b-button size="sm" variant="success" @click="allocateAssets">Allocate Assets</b-button>
          <b-button size="sm" variant="warning" @click="terminateAllocation">Terminate Allocation</b-button>
          <b-button size="sm" variant="danger" @click="destroySolution">Destroy</b-button>
        </b-row>
        <b-row>
          <b-col cols="6">
            <b-table striped hover :items="allocations"></b-table>
          </b-col>
          <b-col cols="6">
          </b-col>
        </b-row>
      </b-tab>
      <b-tab title="Info">
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
      </b-tab>
      <b-tab title="Correlation Matrix">
        <b-row>
          <h1>{{ title }}</h1>
        </b-row>
        <b-row>
          <b-col>
            <b-table striped hover :items="matrix"></b-table>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
  </b-card>
</template>

<script>
import axios from 'axios'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

export default {
  name: 'portfolio',
  data () {
    return {
      title: 'Portfolio ' + this.$route.params.id,
      allocations: [],
      regions: [],
      sectors: [],
      assets: [],
      matrix: []
    }
  },
  created () {
    this.portfolioId = this.$route.params.id

    const socket = new SockJS('/stomp')
    const client = Stomp.over(socket)
    client.debug = null
    let self = this
    client.connect({}, frame => {
      console.log('Connected: ' + frame)
      client.subscribe('/user/queue/solution', message => {
        const data = JSON.parse(message.body)
        self.allocations = data.map(e => ({asset: e.assetClass.name, quantityMillis: e.quantityMillis}))
        console.log(data)
      })
    })

    axios.get(`/api/portfolio/${this.portfolioId}.json`).then(res => {
      this.portfolio = res.data
      this.title = `Portfolio ${res.data.name}`
      this.regions = res.data.regionList
      const regionsId = this.regions.reduce((acc, e) => { acc[e.id] = e; return acc }, {})

      this.sectors = res.data.sectorList
      const sectorsId = this.sectors.reduce((acc, e) => { acc[e.id] = e; return acc }, {})

      this.assets = res.data.assetClassList.map(e => ({
        id: e.id,
        name: e.name,
        region: regionsId[e.regionId].name,
        sector: sectorsId[e.sectorId].name,
        expectedReturnMillis: e.expectedReturnMillis,
        standardDeviationRiskMillis: e.standardDeviationRiskMillis}))
      const assetsId = this.assets.reduce((acc, e) => { acc[e.id] = e; return acc }, {})

      this.matrix = res.data.assetClassList.map(e => {
        const corr = e.correlationMillisMap
        return {name: e.name, ...corr}
      })

      this.allocations = res.data.assetClassAllocationList.map(e => ({
        asset: assetsId[e.assetClassId].name,
        quantityMillis: e.quantityMillis ? e.quantityMillis : 0}))
    }, error => {
      console.error(error)
    })
  },
  methods: {
    allocateAssets (evt) {
      const url = `/api/portfolio/${this.portfolioId}/async-allocate`
      console.log(url)
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
