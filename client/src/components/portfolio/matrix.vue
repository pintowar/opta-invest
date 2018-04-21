<template>
  <div id="corr-matrix">
    <b-row>
      <h1>{{ title }}</h1>
    </b-row>
    <b-row>
      <b-col>
        <b-table striped hover :items="matrix"></b-table>
      </b-col>
    </b-row>
  </div>
</template>

<script>
export default {
  name: 'corr-matrix',
  props: ['title', 'portfolio'],
  computed: {
    matrix () {
      const assetClassList = this.portfolio.assetClassList
      if (assetClassList != null) {
        return assetClassList.map(e => {
          const corr = e.correlationMillisMap
          return {name: e.name, ...corr}
        })
      } else return []
    }
  }
}
</script>