<template>
  <b-card no-body>
    <b-tabs card>
      <b-tab title="Allocation" active>
        <allocation :title="title" :portfolio="portfolio" :status="status" @newStatus="changeStatus" />
      </b-tab>
      <b-tab title="Info">
        <assets :title="title" :portfolio="portfolio" :status="status" />
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
      portfolio: {},
      status: 'NOT_SOLVING'
    }
  },
  components: {
    'corr-matrix': CorrMatrix,
    'assets': Assets,
    'allocation': Allocation
  },
  created () {
    this.portfolioId = Number(this.$route.params.id)

    const socket = new SockJS('/stomp')
    this.client = Stomp.over(socket)
    this.client.debug = null
    let self = this
    this.client.connect({}, frame => {
      console.log('Connected: ' + frame)
      self.client.subscribe('/topic/solution', message => {
        const data = JSON.parse(message.body)
        // console.log(data.status)
        if (data.id === this.portfolioId) {
          self.status = data.status
          self.portfolio = data.portfolio
        }
      })
    })

    axios.get(`/api/portfolio/${this.portfolioId}`).then(res => {
      this.status = res.data.status
      this.portfolio = res.data.investment
      this.title = `Portfolio ${this.portfolio.name}`
    }, error => {
      console.error(error)
    })
  },
  beforeDestroy () {
    this.client.disconnect()
  },
  methods: {
    changeStatus (newStatus) {
      this.status = newStatus
    }
  }
}
</script>
