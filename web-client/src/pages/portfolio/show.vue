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
import CorrMatrix from '@/components/portfolio/matrix.vue'
import Assets from '@/components/portfolio/assets.vue'
import Allocation from '@/components/portfolio/allocation.vue'

export default {
  name: 'portfolio',
  data () {
    return {
      title: 'Portfolio ' + this.$route.params.id,
      portfolio: {},
      status: 'NOT_SOLVING',
      sessionId: null,
      client: null
    }
  },
  components: {
    'corr-matrix': CorrMatrix,
    'assets': Assets,
    'allocation': Allocation
  },
  created () {
    this.portfolioId = Number(this.$route.params.id)

    this.$http.get(`/api/session-id`).then(res => {
      this.sessionId = res.data
    })

    this.$http.get(`/api/portfolio/${this.portfolioId}`).then(res => {
      this.status = res.data.status
      this.portfolio = res.data.investment
      this.title = `Portfolio ${this.portfolio.name}`
    }, error => {
      console.error(error)
    })
  },
  beforeDestroy () {
    if(this.client != null) {
      this.client.close()
      this.client = null
    }
  },
  watch: {
    sessionId (val) {
      this.$log.info('Session id: ' + val)
      this.connect()
    }
  },
  methods: {
    changeStatus (newStatus) {
      this.status = newStatus
    },
    connect () {
      const self = this
      if (self.client == null) {
        self.client = new WebSocket("ws://" + location.host + "/solution/" + this.sessionId)
        self.client.onopen = function() {
            self.$log.info("Connected to the web socket")
        }
        self.client.onmessage = function(message) {
            self.$log.info("Got message:")
            const data = JSON.parse(message.data)
            if (data.id === self.portfolioId) {
              self.status = data.status
              self.portfolio = data.portfolio
            }
        };
      }
    }
  }
}
</script>
