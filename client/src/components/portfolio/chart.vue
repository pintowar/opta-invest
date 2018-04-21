<script>
import { Doughnut, mixins } from 'vue-chartjs'
const { reactiveProp } = mixins

export default {
  extends: Doughnut,
  mixins: [reactiveProp],
  name: 'allocation-chart',
  mounted () {
    this.renderChart(this.chartData, {responsive: true, maintainAspectRatio: false, animation: false})
  }
}
</script>