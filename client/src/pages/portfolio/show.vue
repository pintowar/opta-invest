<template>
  <b-card no-body>
    <b-tabs card>
      <b-tab title="Allocation" active>
        <allocation :title="title" :portfolio="portfolio" :rt-allocations="rtAllocations"/>
      </b-tab>
      <b-tab title="Info">
        <assets :title="title" :portfolio="portfolio" />
      </b-tab>
      <b-tab title="Correlation Matrix">
        <corr-matrix :title="title" :portfolio="portfolio" />
      </b-tab>
    </b-tabs>
  </b-card>
</template>

<script>
import axios from 'axios'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import CorrMatrix from '@/components/portfolio/matrix.vue'
import Assets from '@/components/portfolio/assets.vue'
import Allocation from '@/components/portfolio/allocation.vue'

export default {
  name: 'portfolio',
  data () {
    return {
      title: 'Portfolio ' + this.$route.params.id,
      rtAllocations: [],
      portfolio: {}
    }
  },
  components: {
    'corr-matrix': CorrMatrix,
    'assets': Assets,
    'allocation': Allocation
  },
  created () {
    this.portfolioId = this.$route.params.id

    const socket = new SockJS('/stomp')
    this.client = Stomp.over(socket)
    this.client.debug = null
    let self = this
    this.client.connect({}, frame => {
      console.log('Connected: ' + frame)
      self.client.subscribe('/user/queue/solution', message => {
        const data = JSON.parse(message.body)
        console.log(data)
        self.rtAllocations = data.map(e => ({asset: e.assetClass.name, quantityMillis: e.quantityMillis, quantityLabel: e.quantityLabel}))
      })
    })

    axios.get(`/api/portfolio/${this.portfolioId}`).then(res => {
      this.portfolio = res.data
      this.title = `Portfolio ${res.data.name}`
    }, error => {
      console.error(error)
    })
  },
  beforeDestroy () {
    this.client.disconnect()
  }
}
</script>
